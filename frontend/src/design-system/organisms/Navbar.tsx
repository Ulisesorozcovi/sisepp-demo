import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Logo } from '../atoms/Logo';
import { Avatar } from '../atoms/Avatar';
import { useAuth } from '../../context/AuthContext';
import type { UserRole } from '../../types';

interface NavItem {
  to: string;
  label: string;
  roles: UserRole[];
}

const NAV_ITEMS: NavItem[] = [
  { to: '/estructura', label: 'Estructura del PAC', roles: ['DIRECTOR', 'STAFF', 'COORDINADOR'] },
  { to: '/tareas', label: 'Tareas', roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'] },
  { to: '/mis-tareas', label: 'Mis tareas', roles: ['TECNICO'] },
  { to: '/seguimiento', label: 'Seguimiento', roles: ['COORDINADOR', 'DIRECTOR', 'STAFF'] },
  { to: '/financiero', label: 'Financiero', roles: ['DIRECTOR', 'STAFF', 'COORDINADOR'] },
  { to: '/usuarios', label: 'Usuarios', roles: ['DIRECTOR', 'STAFF'] },
];

export function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  const visibleItems = NAV_ITEMS.filter((item) => user && item.roles.includes(user.role));

  return (
    <div className="dashboard-navbar">
      <Logo size={26} />
      <div className="dashboard-navbar-links">
        {visibleItems.map((item) => (
          <Link
            key={item.to}
            to={item.to}
            className={location.pathname.startsWith(item.to) ? 'current' : ''}
            style={{ textDecoration: 'none' }}
          >
            {item.label}
          </Link>
        ))}
      </div>
      <div className="dashboard-navbar-user">
        {user && <Avatar name={user.fullName} />}
        <span>{user?.fullName ?? user?.role}</span>
        <button onClick={handleLogout}>Salir</button>
      </div>
    </div>
  );
}
