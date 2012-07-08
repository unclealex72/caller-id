/**
 * Copyright 2010 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.unclealex.hbase.testing;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.LocalHBaseCluster;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.zookeeper.MiniZooKeeperCluster;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A container that can be used to start and stop HBase and Zookeeper instances
 * for testing.
 * 
 * @author alex
 * 
 */
public class HBaseTestContainer {

  /**
   * An enumeration that can be used to indentify the different ports HBase uses
   * and thus allows them to be set explicitly
   * 
   * @author alex
   * 
   */
  public enum Port {
    /**
     * The port identified by property <code>hbase.master.port</code>.
     */
    MASTER("hbase.master.port"),
    /**
     * The port identified by property <code>hbase.master.info.port</code>.
     */
    MASTER_INFO("hbase.master.info.port"),
    /**
     * The port identified by property <code>hbase.regionserver.port</code>.
     */
    REGIONSERVER("hbase.regionserver.port"),
    /**
     * The port identified by property <code>hbase.regionserver.info.port</code>
     * .
     */
    REGIONSERVER_INFO("hbase.regionserver.info.port"),
    /**
     * The port identified by property <code>hbase.zookeeper.peerport</code>.
     */
    ZOOKEEPER_PEER("hbase.zookeeper.peerport"),
    /**
     * The port identified by property <code>hbase.zookeeper.leaderport</code>.
     */
    ZOOKEEPER_LEADER("hbase.zookeeper.leaderport"),
    /**
     * The port identified by property
     * <code>hbase.zookeeper.property.clientPort</code>.
     */
    ZOOKEEPER_CLIENT("hbase.zookeeper.property.clientPort"),
    /**
     * The port identified by property <code>hbase.rest.port</code>.
     */
    REST("hbase.rest.port");

    /**
     * The name of the property that this port represents.
     */
    private final String propertyName;

    private Port(String propertyName) {
      this.propertyName = propertyName;
    }

    public String getPropertyName() {
      return propertyName;
    }
  }

  /**
   * The logger used for logging.
   */
  private static final Logger LOG = LoggerFactory.getLogger(HBaseTestContainer.class);

  /**
   * The {@link Configuration} used to configure the HBase and Zookeeper
   * servers.
   */
  private final Configuration configuration = HBaseConfiguration.create();

  /**
   * The root directory where all HBase files will be stored.
   */
  private final File rootDir;

  /**
   * The ZooKeeper cluster that will be set up by this container.
   */
  private MiniZooKeeperCluster zooKeeperCluster;

  /**
   * The HBase cluster that will be set up by this container.
   */
  private LocalHBaseCluster cluster;

  /**
   * The ports that have been either dynamically or statically allocated.
   */
  private final Map<Port, Integer> ports = new HashMap<Port, Integer>();

  /**
   * The ports that will be statically allocated.
   */
  private final Map<Port, Integer> fixedPorts = new HashMap<Port, Integer>();

  public HBaseTestContainer() {
    this(new File(new File(".", "target"), "hbase"));
  }

  public HBaseTestContainer(File rootDir) {
    super();
    this.rootDir = rootDir;
  }

  /**
   * Declare that a port needs to be statically allocated.
   * 
   * @param port
   *          The port to allocate.
   * @param portNumber
   *          The port number to allocated.
   * @return <code>this</code>.
   */
  public HBaseTestContainer withPort(Port port, int portNumber) {
    getFixedPorts().put(port, portNumber);
    return this;
  }

  /**
   * Configure ports either statically or dynamically. This method is called
   * recursively (and hence why an {@link Iterator} is an argument) to allow the
   * compiler to correctly check that all port resources are closed.
   * 
   * @param portIterator
   *          An {@link Iterator} for the remaining ports to configure.
   * @throws IOException
   */
  protected void configurePorts(Iterator<Port> portIterator) throws IOException {
    if (portIterator.hasNext()) {
      Port port = portIterator.next();
      Integer portNumber = getFixedPorts().get(port);
      if (portNumber == null) {
        portNumber = 0;
      }
      ServerSocket serverSocket = new ServerSocket(portNumber);
      try {
        int localPort = serverSocket.getLocalPort();
        LOG.info("Using port " + localPort + " for property " + port.getPropertyName());
        getConfiguration().setInt(port.getPropertyName(), localPort);
        getPorts().put(port, localPort);
        configurePorts(portIterator);
      }
      finally {
        serverSocket.close();
      }
    }
  }

  /**
   * Start the HBase servers and wait until everything has been initialised.
   * @return This.
   * @throws Exception
   */
  public HBaseTestContainer start() throws Exception {
    File rootDir = getRootDir().getCanonicalFile();
    if (rootDir.exists()) {
      if (rootDir.isDirectory()) {
        FileUtils.deleteDirectory(rootDir);
      }
      else {
        rootDir.delete();
      }
    }
    configurePorts(Arrays.asList(Port.values()).iterator());
    Map<String, String> fileProperties = new HashMap<String, String>();
    fileProperties.put("hbase.rootdir", "root");
    fileProperties.put("hbase.tmp.dir", "tmp");
    for (Entry<String, String> entry : fileProperties.entrySet()) {
      File dir = new File(rootDir, entry.getValue());
      getConfiguration().set(entry.getKey(), dir.toURI().toURL().toString());
    }
    getConfiguration().set("hbase.zookeeper.property.dataDir", new File(rootDir, "zookeeper").toString());
    MiniZooKeeperCluster zooKeeperCluster = new MiniZooKeeperCluster();
    File zkDataPath = new File(configuration.get("hbase.zookeeper.property.dataDir"));
    int zkClientPort = configuration.getInt("hbase.zookeeper.property.clientPort", 0);
    if (zkClientPort == 0) {
      throw new IOException("No config value for hbase.zookeeper.property.clientPort");
    }
    zooKeeperCluster.setDefaultClientPort(zkClientPort);
    zooKeeperCluster.startup(zkDataPath);
    setZooKeeperCluster(zooKeeperCluster);
    // Need to have the zk cluster shutdown when master is shutdown.
    // Run a subclass that does the zk cluster shutdown on its way out.
    LocalHBaseCluster cluster = new LocalHBaseCluster(configuration, 1, 1, LocalHMaster.class, HRegionServer.class);
    ((LocalHMaster) cluster.getMaster(0)).setZKCluster(zooKeeperCluster);
    cluster.startup();
    setCluster(cluster);
    waitForServerOnline();
    return this;
  }

  /**
   * Block until the master has come online, indicating it is ready to be used.
   */
  protected void waitForServerOnline() {
    HMaster master = getCluster().getActiveMaster();
    while (!master.isInitialized()) {
      try {
        Thread.sleep(100);
      }
      catch (InterruptedException e) {
        // continue waiting
      }
    }
  }

  /**
   * Stop the HBase servers.
   */
  public void stop() {
    HBaseAdmin adm = null;
    try {
      // Don't try more than once
      configuration.setInt("hbase.client.retries.number", 1);
      adm = new HBaseAdmin(configuration);
    }
    catch (MasterNotRunningException e) {
      LOG.error("Master not running");
    }
    catch (ZooKeeperConnectionException e) {
      LOG.error("ZooKeeper not available");
    }
    try {
      adm.shutdown();
    }
    catch (Throwable t) {
      LOG.error("Failed to stop master", t);
    }
  }

  /**
   * Version of master that will shutdown the passed zk cluster on its way out.
   */
  public static class LocalHMaster extends HMaster {
    private MiniZooKeeperCluster zkcluster = null;

    public LocalHMaster(Configuration conf) throws IOException, KeeperException, InterruptedException {
      super(conf);
    }

    @Override
    public void run() {
      super.run();
      if (this.zkcluster != null) {
        try {
          this.zkcluster.shutdown();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    void setZKCluster(final MiniZooKeeperCluster zkcluster) {
      this.zkcluster = zkcluster;
    }
  }

  public MiniZooKeeperCluster getZooKeeperCluster() {
    return zooKeeperCluster;
  }

  protected void setZooKeeperCluster(MiniZooKeeperCluster zooKeeperCluster) {
    this.zooKeeperCluster = zooKeeperCluster;
  }

  protected LocalHBaseCluster getCluster() {
    return cluster;
  }

  protected void setCluster(LocalHBaseCluster cluster) {
    this.cluster = cluster;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public File getRootDir() {
    return rootDir;
  }

  public Map<Port, Integer> getPorts() {
    return ports;
  }

  public Map<Port, Integer> getFixedPorts() {
    return fixedPorts;
  }

  
}
