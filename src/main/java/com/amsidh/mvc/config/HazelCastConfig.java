package com.amsidh.mvc.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.KubernetesConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelCastConfig {

    @Value("${hazelcast.group.name:your-hazelcast-group-name}")
    private String groupName;

    @Value("${hazelcast.network.join.multicast.enabled:false}")
    private Boolean multicastEnabled;

    @Value("${hazelcast.network.join.kubernetes.enabled}")
    private Boolean kubernetesEnabled;

    @Value("${hazelcast.network.join.kubernetes.service-port:5701}")
    private Integer kubernetesServicePort;

    @Value("${hazelcast.integrity-checker.enabled:true}")
    private Boolean integrityCheckerEnabled;

    @Value("${hazelcast.diagnostics.enabled:true}")
    private Boolean diagnosticsEnabled;

    @Value("${hazelcast.metrics.enabled:true}")
    private Boolean metricsEnabled;

    // We need to specify the name otherwise it can conflict with internal Hazelcast beans
    @Bean("hazelcastInstance")
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName(groupName);
        config.setProperty("hazelcast.metrics.enabled", metricsEnabled.toString());
        config.setProperty("hazelcast.integrity-checker.enabled", integrityCheckerEnabled.toString());
        config.setProperty("hazelcast.diagnostics.enabled", diagnosticsEnabled.toString());

        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPortAutoIncrement(false);
        networkConfig.getJoin().getMulticastConfig().setEnabled(multicastEnabled);

        KubernetesConfig kubernetesConfig = networkConfig.getJoin().getKubernetesConfig();
        kubernetesConfig.setEnabled(kubernetesEnabled);
        kubernetesConfig.setProperty("service-port", kubernetesServicePort.toString());
        kubernetesConfig.setUsePublicIp(false);
        return Hazelcast.newHazelcastInstance(config);
    }

}
