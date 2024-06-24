package org.example.service;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class HazelcastConfiguration {

    @Bean
    public HazelcastInstance hazelcastInstance(@Value("${hazelcast.network.join.tcp-ip.members}") List<String> members) {
        Config config = new Config();
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.getJoin().getMulticastConfig().setEnabled(false);
        networkConfig.getJoin().getTcpIpConfig().setEnabled(true).setMembers(members);

        return Hazelcast.newHazelcastInstance(config);
    }
}
