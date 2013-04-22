package uk.co.unclealex.callerid.remote.dao

import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * A simple base class for DAO tests that contains all the required configuration.
 */
@Transactional
@TransactionConfiguration
@RunWith(value = classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(
  locations = Array(
    "classpath*:application-context-callerid-persistence.xml",
    "classpath*:application-context-callerid-persistence-test.xml"))
abstract class AbstractDaoTest {

  implicit class InitialiserImplicit[V](value: V) {

    def init(initialiser: V => Unit) = {
      initialiser.apply(value)
      value
    }
  }
}