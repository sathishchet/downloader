package org.saravana;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.saravana.consumer.DownloadConsumerTest;
import org.saravana.domain.DownloadTaskTest;
import org.saravana.producer.DownloadProducerTest;
import org.saravana.service.DownloadServiceTest;
import org.saravana.util.URLHandlerFactoryTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	DownloaderAppTest.class,
	URLHandlerFactoryTest.class, 
	DownloadServiceTest.class, 
	DownloadProducerTest.class,
	DownloadConsumerTest.class,
	DownloadTaskTest.class,
	DownloadConsumerTest.class})
public class DownloaderTest {

}
