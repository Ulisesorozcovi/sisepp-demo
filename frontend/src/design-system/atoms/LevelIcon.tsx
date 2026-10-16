export type TreeLevel = 'linea' | 'programa' | 'proyecto' | 'actividad';

const LEVEL_CONFIG: Record<TreeLevel, { emoji: string; className: string }> = {
  linea: { emoji: '\u{1F3DB}️', className: 'level-icon--linea' },
  programa: { emoji: '\u{1F4CB}', className: 'level-icon--programa' },
  proyecto: { emoji: '\u{1F4C1}', className: 'level-icon--proyecto' },
  actividad: { emoji: '\u{1F4CC}', className: 'level-icon--actividad' },
};

interface Props {
  level: TreeLevel;
}

export function LevelIcon({ level }: Props) {
  const { emoji, className } = LEVEL_CONFIG[level];
  return <span className={`level-icon ${className}`}>{emoji}</span>;
}
