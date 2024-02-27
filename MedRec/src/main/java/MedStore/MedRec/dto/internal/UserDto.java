package MedStore.MedRec.dto.internal;

import MedStore.MedRec.enums.Role;

public record UserDto(long userId, Role role, Long divisionId) {
}
