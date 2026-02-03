package ru.otus.hw.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;

import java.util.Map;

@Profile("test")
@TestConfiguration
public class TestShellConfig {
    @Bean
    @Primary
    public ShellRunner shellRunner() {
        return new ShellRunner() {
            @Override
            public boolean canRun(ApplicationArguments args) {
                return false;
            }

            @Override
            public void run(ApplicationArguments args) throws Exception {

                System.out.println("Shell is disabled for tests");
            }
        };
    }

    @Bean
    @Primary
    public CommandCatalog commandCatalog() {
        return new CommandCatalog() {
            @Override
            public void register(CommandRegistration... registration) {

            }

            @Override
            public void unregister(CommandRegistration... registration) {

            }

            @Override
            public void unregister(String... commandName) {

            }

            @Override
            public Map<String, CommandRegistration> getRegistrations() {
                return Map.of();
            }
        };
    }
}
