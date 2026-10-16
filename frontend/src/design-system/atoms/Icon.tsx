interface Props {
  name: IconName;
  size?: number;
}

export type IconName =
  | 'home'
  | 'planning'
  | 'structure'
  | 'task-catalog'
  | 'schedule'
  | 'progress'
  | 'chart'
  | 'calendar'
  | 'project'
  | 'program'
  | 'financial'
  | 'users'
  | 'my-tasks'
  | 'chevron-left'
  | 'chevron-right'
  | 'chevron-down'
  | 'chevron-up'
  | 'expand-all'
  | 'collapse-all'
  | 'filter'
  | 'plus'
  | 'report'
  | 'logout'
  | 'menu'
  | 'edit'
  | 'target';

const PATHS: Record<IconName, string> = {
  home: 'M3 12l9-8 9 8v9a1 1 0 01-1 1h-5v-5h-6v5H4a1 1 0 01-1-1v-9z',
  planning: 'M19 3H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2V5a2 2 0 00-2-2zM9 17H7v-2h2v2zm0-4H7v-2h2v2zm0-4H7V7h2v2zm8 8h-6v-2h6v2zm0-4h-6v-2h6v2zm0-4h-6V7h6v2z',
  structure: 'M4 4h6v6H4zM14 4h6v6h-6zM9 14h6v6H9z',
  'task-catalog': 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2M9 12h6M9 16h6',
  schedule: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
  progress: 'M13 17l5-5-5-5M6 17l5-5-5-5',
  chart: 'M9 19v-6M15 19v-8M21 19V9M3 19v-4',
  calendar: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
  project: 'M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z',
  program: 'M4 6h16M4 12h16M4 18h10',
  financial: 'M12 2a10 10 0 100 20 10 10 0 000-20zm0 4v4l3 3',
  users: 'M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2M9 11a4 4 0 100-8 4 4 0 000 8zM23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75',
  'my-tasks': 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M12 11l2 2 4-4',
  'chevron-left': 'M15 18l-6-6 6-6',
  'chevron-right': 'M9 18l6-6-6-6',
  'chevron-down': 'M6 9l6 6 6-6',
  'chevron-up': 'M18 15l-6-6-6 6',
  'expand-all': 'M4 4h16v4H4zM4 10h16v4H4zM4 16h16v4H4z',
  'collapse-all': 'M4 6h16M4 12h16M4 18h16',
  filter: 'M22 3H2l8 9.46V19l4 2v-8.54L22 3z',
  plus: 'M12 5v14M5 12h14',
  report: 'M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8l-6-6zM14 2v6h6M16 13H8M16 17H8M10 9H8',
  logout: 'M17 16l4-4-4-4M21 12H9M12 21H5a2 2 0 01-2-2V5a2 2 0 012-2h7',
  menu: 'M4 6h16M4 12h16M4 18h16',
  edit: 'M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7M18.5 2.5a2.12 2.12 0 013 3L12 15l-4 1 1-4 9.5-9.5z',
  target: 'M12 22a10 10 0 100-20 10 10 0 000 20zM12 18a6 6 0 100-12 6 6 0 000 12zM12 14a2 2 0 100-4 2 2 0 000 4z',
};

export function Icon({ name, size = 20 }: Props) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={2}
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d={PATHS[name]} />
    </svg>
  );
}
