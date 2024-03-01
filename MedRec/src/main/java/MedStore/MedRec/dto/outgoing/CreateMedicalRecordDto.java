package MedStore.MedRec.dto.outgoing;

public record CreateMedicalRecordDto(long recordId, long patientId, long nurseId, long doctorId, long divisionId,
        String note) {
}
