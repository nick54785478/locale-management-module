package com.example.demo.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.port.LocaleConfigRepositoryPort;
import com.example.demo.application.shared.command.inbound.CreateLocaleConfigCommand;
import com.example.demo.application.shared.command.inbound.UpdateLocaleConfigCommand;
import com.example.demo.application.shared.command.outbound.CreateLocaleConfigPortCommand;
import com.example.demo.application.shared.command.outbound.UpdateLocaleConfigPortCommand;
import com.example.demo.application.shared.exception.LocaleAlreadyExistsException;
import com.example.demo.application.shared.exception.LocaleNotFoundException;

@ExtendWith(MockitoExtension.class)
class LocaleConfigCommandServiceTest {

    @Mock
    private LocaleConfigRepositoryPort repositoryPort;

    @InjectMocks
    private LocaleConfigCommandService commandService;

    @Test
    void create_WhenLocaleDoesNotExist_ShouldSaveThroughPort() {
        // Arrange
        CreateLocaleConfigCommand command = new CreateLocaleConfigCommand("zh-TW", "繁體中文", true, "測試用");
        when(repositoryPort.existsByCode("zh_tw")).thenReturn(false);

        // Act
        commandService.create(command);

        // Assert
        verify(repositoryPort).existsByCode("zh_tw");
        verify(repositoryPort).createConfig(any(CreateLocaleConfigPortCommand.class));
    }

    @Test
    void create_WhenLocaleAlreadyExists_ShouldThrowException() {
        // Arrange
        CreateLocaleConfigCommand command = new CreateLocaleConfigCommand("zh-TW", "繁體中文", true, "測試用");
        when(repositoryPort.existsByCode("zh_tw")).thenReturn(true);

        // Act & Assert
        assertThrows(LocaleAlreadyExistsException.class, () -> commandService.create(command));
        verify(repositoryPort, never()).createConfig(any());
    }

    @Test
    void update_WhenLocaleExists_ShouldUpdateThroughPort() {
        // Arrange
        UpdateLocaleConfigCommand command = new UpdateLocaleConfigCommand("en-US", "English (US)", false, "停用英文");
        when(repositoryPort.existsByCode("en_us")).thenReturn(true);

        // Act
        commandService.update(command);

        // Assert
        verify(repositoryPort).existsByCode("en_us");
        verify(repositoryPort).updateConfig(any(UpdateLocaleConfigPortCommand.class));
    }

    @Test
    void update_WhenLocaleDoesNotExist_ShouldThrowException() {
        // Arrange
        UpdateLocaleConfigCommand command = new UpdateLocaleConfigCommand("en-US", "English (US)", false, "停用英文");
        when(repositoryPort.existsByCode("en_us")).thenReturn(false);

        // Act & Assert
        assertThrows(LocaleNotFoundException.class, () -> commandService.update(command));
        verify(repositoryPort, never()).updateConfig(any());
    }
}
