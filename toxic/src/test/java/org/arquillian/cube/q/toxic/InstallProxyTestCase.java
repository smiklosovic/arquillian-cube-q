package org.arquillian.cube.q.toxic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.q.core.InstallProxy;
import org.arquillian.cube.q.spi.ProxyManager;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.core.test.AbstractManagerTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InstallProxyTestCase extends AbstractManagerTestBase {

    private static final String CONTENT = 
            "a:\n" +
            "  image: a/a\n" +
            "  portBindings: [8089/tcp]\n" +
            "  links:\n" +
            "    - b:b\n" +
            "b:\n" +
            "  image: b/b\n" +
            "  exposedPorts: [2112/tcp]\n";

    
    @Mock
    private ServiceLoader loader;
    
    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(InstallProxy.class);
    }

    @Before
    public void setup() {
        Mockito.when(loader.onlyOne(ProxyManager.class)).thenReturn(new ToxicProxyHandler());
        bind(ApplicationScoped.class, ServiceLoader.class, loader);
    }
    
    @Test
    public void shouldInstallProxy() throws Exception {
        CubeDockerConfiguration config = createConfig(CONTENT);
        fire(config);
        
        DockerCompositions cubes = config.getDockerContainersContent();
        Assert.assertEquals(3, cubes.getContainerIds().size());
        
        System.out.println(config.toString());
    }

    private CubeDockerConfiguration createConfig(String content) {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("serverVersion", "1.13");
        parameters.put("serverUri", "http://localhost:25123");
        parameters.put("dockerContainers", content);

        return CubeDockerConfiguration.fromMap(parameters, null);
    }
}
