import { useState, useEffect, useRef } from 'react';
import { notificationApi, type AppNotification } from '../../api/notificationApi';

function timeAgo(dateStr: string): string {
  const diff = Date.now() - new Date(dateStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return 'ahora';
  if (mins < 60) return `hace ${mins} min`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24) return `hace ${hrs}h`;
  const days = Math.floor(hrs / 24);
  return `hace ${days}d`;
}

export function NotificationBell() {
  const [unreadCount, setUnreadCount] = useState(0);
  const [notifications, setNotifications] = useState<AppNotification[]>([]);
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    notificationApi.unreadCount().then(setUnreadCount).catch(() => {});
    const interval = setInterval(() => {
      notificationApi.unreadCount().then(setUnreadCount).catch(() => {});
    }, 30000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  async function handleToggle() {
    if (!open) {
      const data = await notificationApi.list().catch(() => [] as AppNotification[]);
      setNotifications(data);
    }
    setOpen(!open);
  }

  async function handleMarkAsRead(id: string) {
    await notificationApi.markAsRead(id).catch(() => {});
    setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)));
    setUnreadCount((c) => Math.max(0, c - 1));
  }

  async function handleMarkAllRead() {
    await notificationApi.markAllAsRead().catch(() => {});
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    setUnreadCount(0);
  }

  return (
    <div className="notification-bell" ref={ref}>
      <button className="notification-bell__trigger" onClick={handleToggle} title="Notificaciones">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>
        {unreadCount > 0 && <span className="notification-bell__badge">{unreadCount > 9 ? '9+' : unreadCount}</span>}
      </button>

      {open && (
        <div className="notification-bell__dropdown">
          <div className="notification-bell__header">
            <strong>Notificaciones</strong>
            {unreadCount > 0 && (
              <button className="notification-bell__mark-all" onClick={handleMarkAllRead}>
                Marcar todas como leídas
              </button>
            )}
          </div>
          <div className="notification-bell__list">
            {notifications.length === 0 && (
              <p className="notification-bell__empty">No tienes notificaciones</p>
            )}
            {notifications.map((n) => (
              <div
                key={n.id}
                className={`notification-bell__item ${!n.read ? 'notification-bell__item--unread' : ''}`}
                onClick={() => !n.read && handleMarkAsRead(n.id)}
              >
                <div className="notification-bell__item-icon">
                  {n.type === 'COORDINATOR_ASSIGNED' ? '📋' : '✅'}
                </div>
                <div className="notification-bell__item-content">
                  <strong>{n.title}</strong>
                  <p>{n.message}</p>
                  <span className="notification-bell__item-time">{timeAgo(n.createdAt)}</span>
                </div>
                {!n.read && <span className="notification-bell__dot" />}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
