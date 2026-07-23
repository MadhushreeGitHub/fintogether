package com.fintogether.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * The purpose is just to activate the JPA auditing infrastructure globally.
 * When you add auditor tracking later, this is where the AuditorAware<UUID> bean will go.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
