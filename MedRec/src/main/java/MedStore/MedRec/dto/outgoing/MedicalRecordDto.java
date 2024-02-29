package MedStore.MedRec.dto.outgoing;

public record MedicalRecordDto(long recordId, long patientId, long nurseId, long doctorId, long divisionId, String note) {
}
