import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Icon, type IconName } from '../atoms/Icon';
import { Logo } from '../atoms/Logo';
import { NotificationBell } from './NotificationBell';
import type { UserRole } from '../../types';

interface NavItem {
  to: string;
  label: string;
  icon: IconName;
  roles: UserRole[];
}

const NAV_ITEMS: NavItem[] = [
  { to: '/', label: 'Inicio', icon: 'home', roles: ['DIRECTOR', 'STAFF', 'COORDINADOR', 'TECNICO'] },
  { to: '/instrumentos', label: 'PGAR / PAC / POAI', icon: 'planning', roles: ['DIRECTOR', 'STAFF'] },
  { to: '/estructura', label: 'Estructura PAC', icon: 'structure', roles: ['DIRECTOR', 'STAFF', 'COORDINADOR'] },
  { to: '/catalogo-tareas', label: 'Catálogo de Tareas', icon: 'task-catalog', roles: ['COORDINADOR', 'STAFF'] },
  { to: '/cronograma', label: 'Cronograma', icon: 'schedule', roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'] },
  { to: '/seguimiento', label: 'Seguimiento', icon: 'progress', roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'] },
  { to: '/registrar-avance', label: 'Registrar Avance', icon: 'edit', roles: ['COORDINADOR', 'STAFF', 'TECNICO'] },
  { to: '/mis-tareas', label: 'Mis Tareas', icon: 'my-tasks', roles: ['TECNICO'] },
  { to: '/consulta-metas', label: 'Consulta de Metas', icon: 'target', roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'] },
  { to: '/consulta-tareas', label: 'Consulta de Tareas', icon: 'task-catalog', roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'] },
  { to: '/financiero', label: 'Financiero', icon: 'financial', roles: ['DIRECTOR', 'STAFF', 'COORDINADOR'] },
  { to: '/reportes', label: 'Reportes', icon: 'report', roles: ['DIRECTOR', 'STAFF', 'COORDINADOR'] },
  { to: '/reporte-consolidado', label: 'Reporte Consolidado', icon: 'chart', roles: ['DIRECTOR', 'STAFF', 'COORDINADOR'] },
  { to: '/usuarios', label: 'Usuarios', icon: 'users', roles: ['DIRECTOR', 'STAFF'] },
];

export function Sidebar() {
  const [collapsed, setCollapsed] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  const visibleItems = NAV_ITEMS.filter((item) => user && item.roles.includes(user.role));

  return (
    <aside className={`sidebar ${collapsed ? 'sidebar--collapsed' : ''}`}>
      <div className="sidebar-header">
        <Logo size={28} withLabel={!collapsed} />
        <button
          className="sidebar-toggle"
          onClick={() => setCollapsed((c) => !c)}
          title={collapsed ? 'Expandir menú' : 'Contraer menú'}
        >
          <Icon name={collapsed ? 'chevron-right' : 'chevron-left'} size={18} />
        </button>
      </div>

      <nav className="sidebar-nav">
        {visibleItems.map((item) => {
          const isActive =
            item.to === '/'
              ? location.pathname === '/'
              : location.pathname.startsWith(item.to);
          return (
            <Link
              key={item.to}
              to={item.to}
              className={`sidebar-item ${isActive ? 'sidebar-item--active' : ''}`}
              title={collapsed ? item.label : undefined}
            >
              <Icon name={item.icon} size={20} />
              {!collapsed && <span>{item.label}</span>}
            </Link>
          );
        })}
      </nav>

      <div className="sidebar-footer">
        <div className={`sidebar-notifications ${collapsed ? 'sidebar-notifications--collapsed' : ''}`}>
          <NotificationBell />
          {!collapsed && <span style={{ fontSize: 13, color: 'rgba(255,255,255,.7)' }}>Notificaciones</span>}
        </div>
        {user && (
          <div className="sidebar-user" title={collapsed ? user.fullName : undefined}>
            <div className="sidebar-user-avatar">
              {user.fullName.split(' ').map((n) => n[0]).join('').slice(0, 2).toUpperCase()}
            </div>
            {!collapsed && (
              <div className="sidebar-user-info">
                <span className="sidebar-user-name">{user.fullName}</span>
                <span className="sidebar-user-role">{user.role}</span>
              </div>
            )}
          </div>
        )}
        <button
          className="sidebar-item sidebar-logout"
          onClick={handleLogout}
          title={collapsed ? 'Cerrar sesión' : undefined}
        >
          <Icon name="logout" size={20} />
          {!collapsed && <span>Cerrar sesión</span>}
        </button>
      </div>
    </aside>
  );
}
