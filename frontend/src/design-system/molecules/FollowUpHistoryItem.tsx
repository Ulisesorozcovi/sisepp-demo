import type { FollowUp, FollowUpStatus } from '../../types';

const STATUS_LABELS: Record<FollowUpStatus, string> = {
  EN_PROCESO: 'En proceso',
  CUMPLIDA: 'Cumplida',
  REZAGADA: 'Rezagada',
  NO_APLICA: 'No aplica',
};

const MONTH_NAMES = [
  '', 'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
  'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic',
];

interface Props {
  followUp: FollowUp;
}

export function FollowUpHistoryItem({ followUp }: Props) {
  return (
    <div className="followup-history-item">
      <div className="followup-history-period">
        {MONTH_NAMES[followUp.reportedMonth]} {followUp.reportedYear}
      </div>
      <div className="followup-history-progress">
        Avance: {followUp.physicalProgress ?? 0}
      </div>
      <span className={`followup-status-pill followup-status-pill--${followUp.status.toLowerCase()}`}>
        {STATUS_LABELS[followUp.status]}
      </span>
      {followUp.evidenceUrl && (
        <a
          className="followup-history-evidence"
          href={followUp.evidenceUrl}
          target="_blank"
          rel="noopener noreferrer"
        >
          Ver evidencia
        </a>
      )}
      {followUp.observations && (
        <div className="followup-history-obs">{followUp.observations}</div>
      )}
    </div>
  );
}
