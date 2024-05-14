package com.tsukihi;

import com.tsukihi.myrpc.registry.RegistryTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    RegistryTest registryTest = new RegistryTest();

    public void testRegistry() throws Exception {
        registryTest.init();
        registryTest.register();
        registryTest.unRegister();
        registryTest.serviceDiscovery();
        registryTest.heartbeat();
    }



//    public void testUnRegister() {
//        registryTest.unRegister();
//    }
//
//    public void testServiceDiscovery() {
//        registryTest.serviceDiscovery();
//    }
//

}
