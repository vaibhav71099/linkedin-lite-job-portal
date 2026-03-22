import { useEffect, useState } from "react";
import api from "../api";

function relativeTime(value) {
  if (!value) {
    return "";
  }
  const date = new Date(value).getTime();
  const diffMinutes = Math.max(1, Math.floor((Date.now() - date) / 60000));
  if (diffMinutes < 60) return `${diffMinutes}m`;
  const diffHours = Math.floor(diffMinutes / 60);
  if (diffHours < 24) return `${diffHours}h`;
  const diffDays = Math.floor(diffHours / 24);
  return `${diffDays}d`;
}

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadNotifications();
  }, []);

  async function loadNotifications() {
    setLoading(true);
    try {
      const response = await api.get("/api/notifications");
      setNotifications(response.data.data);
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load notifications.");
    } finally {
      setLoading(false);
    }
  }

  async function handleMarkAsRead(notificationId) {
    try {
      await api.post(`/api/notifications/${notificationId}/read`);
      setNotifications((current) =>
        current.map((item) => (item.id === notificationId ? { ...item, read: true } : item))
      );
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to update notification.");
    }
  }

  return (
    <section className="page-section">
      <div className="content-feed">
        <section className="feed-card">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Notifications</p>
              <h2>Keep up with your network and activity</h2>
              <p className="support-text">
                Connection updates, reactions, comments, and messages appear here.
              </p>
            </div>
          </div>

          {message && <p className="message error">{message}</p>}
          {loading && <p className="support-text">Loading notifications...</p>}

          {!loading && notifications.length === 0 && (
            <div className="empty-state">
              <strong>No notifications yet</strong>
              <p className="support-text">As your network grows and activity happens, this list will fill up.</p>
            </div>
          )}

          <div className="notification-list">
            {notifications.map((notification) => (
              <article
                key={notification.id}
                className={`notification-card ${notification.read ? "" : "unread"}`}
              >
                <div className="notification-top">
                  <div className="profile-mini-avatar">
                    {(notification.actor?.name || "N").slice(0, 1).toUpperCase()}
                  </div>
                  <div className="notification-copy">
                    <strong>{notification.title}</strong>
                    <p>{notification.body}</p>
                  </div>
                  <span className="sidebar-text">{relativeTime(notification.createdAt)}</span>
                </div>

                {!notification.read && (
                  <button
                    type="button"
                    className="ghost-button inline-button"
                    onClick={() => handleMarkAsRead(notification.id)}
                  >
                    Mark as read
                  </button>
                )}
              </article>
            ))}
          </div>
        </section>
      </div>

      <aside className="right-rail">
        <section className="sidebar-card">
          <p className="sidebar-heading">What appears here</p>
          <p className="sidebar-text">
            Invitations, accepted requests, post reactions, comments, and messages all route into this center.
          </p>
        </section>
      </aside>
    </section>
  );
}
