import type { ReactNode } from 'react';
import { Sidebar } from '../organisms/Sidebar';

export function DashboardLayout({ children }: { children: ReactNode }) {
  return (
    <div className="app-layout">
      <Sidebar />
      <main className="app-main">
        {children}
      </main>
    </div>
  );
}
