package uk.co.unclealex.callerid.remote.dao

import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 * A simple base class for DAO tests that contains all the required configuration.
 */
@Transactional
@TransactionConfiguration
@RunWith(typeof(SpringJUnit4ClassRunner))
@ContextConfiguration(value = #["classpath*:application-context-callerid.xml", "classpath*:application-context-callerid-test.xml"])
abstract class AbstractDaoTest {

}