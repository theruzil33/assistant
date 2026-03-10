package com.theruzil.life_tracking.service;

import com.theruzil.life_tracking.entity.Tracking;
import com.theruzil.life_tracking.repository.TrackingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    private TrackingRepository trackingRepository;

    @InjectMocks
    private TrackingService trackingService;

    private Tracking tracking;

    @BeforeEach
    void setUp() {
        tracking = new Tracking();
        tracking.setId(1L);
        tracking.setTitle("Марафон");
        tracking.setStartDate(LocalDate.of(2026, 4, 1));
        tracking.setEndDate(LocalDate.of(2026, 6, 30));
        tracking.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAll_returnsAllTrackings() {
        when(trackingRepository.findAll()).thenReturn(List.of(tracking));

        List<Tracking> result = trackingService.getAll();

        assertThat(result).hasSize(1).contains(tracking);
    }

    @Test
    void getById_existingId_returnsTracking() {
        when(trackingRepository.findById(1L)).thenReturn(Optional.of(tracking));

        Tracking result = trackingService.getById(1L);

        assertThat(result).isEqualTo(tracking);
    }

    @Test
    void getById_notExistingId_throwsException() {
        when(trackingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackingService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsTracking() {
        when(trackingRepository.save(tracking)).thenReturn(tracking);

        Tracking result = trackingService.create(tracking);

        assertThat(result).isEqualTo(tracking);
        verify(trackingRepository).save(tracking);
    }

    @Test
    void create_withoutDates_savesAndReturnsTracking() {
        Tracking withoutDates = new Tracking();
        withoutDates.setTitle("Бег");

        when(trackingRepository.save(withoutDates)).thenReturn(withoutDates);

        Tracking result = trackingService.create(withoutDates);

        assertThat(result.getStartDate()).isNull();
        assertThat(result.getEndDate()).isNull();
        verify(trackingRepository).save(withoutDates);
    }

    @Test
    void update_existingId_updatesFields() {
        Tracking updated = new Tracking();
        updated.setTitle("Полумарафон");
        updated.setStartDate(LocalDate.of(2026, 5, 1));
        updated.setEndDate(LocalDate.of(2026, 5, 31));

        when(trackingRepository.findById(1L)).thenReturn(Optional.of(tracking));
        when(trackingRepository.save(tracking)).thenReturn(tracking);

        Tracking result = trackingService.update(1L, updated);

        assertThat(result.getTitle()).isEqualTo("Полумарафон");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2026, 5, 31));
        verify(trackingRepository).save(tracking);
    }

    @Test
    void update_clearsDates_whenUpdatedHasNullDates() {
        Tracking updated = new Tracking();
        updated.setTitle("Бег без срока");
        updated.setStartDate(null);
        updated.setEndDate(null);

        when(trackingRepository.findById(1L)).thenReturn(Optional.of(tracking));
        when(trackingRepository.save(tracking)).thenReturn(tracking);

        Tracking result = trackingService.update(1L, updated);

        assertThat(result.getStartDate()).isNull();
        assertThat(result.getEndDate()).isNull();
        verify(trackingRepository).save(tracking);
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        trackingService.delete(1L);

        verify(trackingRepository).deleteById(1L);
    }
}
