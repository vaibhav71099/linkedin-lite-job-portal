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

export default function MessagingPage() {
  const [connections, setConnections] = useState([]);
  const [conversations, setConversations] = useState([]);
  const [messages, setMessages] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [selectedConversationId, setSelectedConversationId] = useState(null);
  const [draft, setDraft] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);

  useEffect(() => {
    loadMessaging();
  }, []);

  async function loadMessaging() {
    setLoading(true);
    try {
      const [connectionsResponse, conversationsResponse] = await Promise.all([
        api.get("/api/network/connections"),
        api.get("/api/messages/conversations")
      ]);
      setConnections(connectionsResponse.data.data);
      const nextConversations = conversationsResponse.data.data;
      setConversations(nextConversations);

      if (nextConversations.length > 0) {
        await openConversation(nextConversations[0]);
      } else if (connectionsResponse.data.data.length > 0) {
        setSelectedUser(connectionsResponse.data.data[0]);
      }
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load inbox.");
    } finally {
      setLoading(false);
    }
  }

  async function openConversation(conversation) {
    setSelectedConversationId(conversation.id);
    setSelectedUser(conversation.otherParticipant);
    try {
      const response = await api.get(`/api/messages/conversations/${conversation.id}`);
      setMessages(response.data.data);
      const conversationsResponse = await api.get("/api/messages/conversations");
      setConversations(conversationsResponse.data.data);
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load conversation.");
    }
  }

  async function handleSend(event) {
    event.preventDefault();
    if (!selectedUser || !draft.trim()) {
      return;
    }

    setSending(true);
    setMessage("");
    try {
      await api.post(`/api/messages/users/${selectedUser.id}`, { content: draft });
      setDraft("");
      const [connectionsResponse, conversationsResponse] = await Promise.all([
        api.get("/api/network/connections"),
        api.get("/api/messages/conversations")
      ]);
      setConnections(connectionsResponse.data.data);
      const nextConversations = conversationsResponse.data.data;
      setConversations(nextConversations);
      const matchingConversation = nextConversations.find(
        (conversation) => conversation.otherParticipant.id === selectedUser.id
      );
      if (matchingConversation) {
        await openConversation(matchingConversation);
      }
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to send message.");
    } finally {
      setSending(false);
    }
  }

  return (
    <section className="page-section">
      <div className="content-feed">
        <section className="feed-card messaging-shell">
          <div className="messaging-sidebar">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Messaging</p>
                <h3>Inbox</h3>
              </div>
            </div>

            <div className="conversation-list">
              {conversations.map((conversation) => (
                <button
                  key={conversation.id}
                  type="button"
                  className={`conversation-item ${selectedConversationId === conversation.id ? "active" : ""}`}
                  onClick={() => openConversation(conversation)}
                >
                  <div className="profile-mini-avatar">
                    {(conversation.otherParticipant.name || "U").slice(0, 1).toUpperCase()}
                  </div>
                  <div className="conversation-copy">
                    <strong>{conversation.otherParticipant.name}</strong>
                    <p>{conversation.lastMessagePreview}</p>
                  </div>
                  <div className="conversation-meta">
                    <span>{relativeTime(conversation.updatedAt)}</span>
                    {conversation.unreadCount > 0 && <span className="notification-dot">{conversation.unreadCount}</span>}
                  </div>
                </button>
              ))}
            </div>
          </div>

          <div className="messaging-main">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Direct messages</p>
                <h3>{selectedUser?.name || "Choose a connection"}</h3>
                <p className="support-text">{selectedUser?.headline || "You can message accepted connections here."}</p>
              </div>
            </div>

            {loading && <p className="support-text">Loading inbox...</p>}

            {!loading && conversations.length === 0 && (
              <div className="empty-state">
                <strong>No conversations yet</strong>
                <p className="support-text">Send the first message to one of your connections below.</p>
              </div>
            )}

            <div className="message-thread">
              {messages.map((item) => (
                <article
                  key={item.id}
                  className={`message-bubble ${item.sender.id === selectedUser?.id ? "incoming" : "outgoing"}`}
                >
                  <strong>{item.sender.name}</strong>
                  <p>{item.content}</p>
                </article>
              ))}
            </div>

            {selectedUser && (
              <form className="comment-composer" onSubmit={handleSend}>
                <input
                  type="text"
                  value={draft}
                  onChange={(event) => setDraft(event.target.value)}
                  placeholder={`Message ${selectedUser.name}`}
                />
                <button type="submit" className="primary-button inline-button" disabled={sending}>
                  {sending ? "Sending..." : "Send"}
                </button>
              </form>
            )}

            {message && <p className="message error">{message}</p>}
          </div>
        </section>

        <section className="feed-card">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Connections</p>
              <h3>Start a conversation</h3>
            </div>
          </div>

          <div className="network-grid">
            {connections.map((person) => (
              <button
                key={person.id}
                type="button"
                className="network-card network-button"
                onClick={() => {
                  setSelectedUser(person);
                  setSelectedConversationId(null);
                  setMessages([]);
                }}
              >
                <div className="network-card-top">
                  <div className="profile-avatar">{(person.name || "U").slice(0, 1).toUpperCase()}</div>
                  <div>
                    <h3>{person.name}</h3>
                    <p className="network-headline">{person.headline || "Professional contact"}</p>
                    <p className="sidebar-text">{[person.currentCompany, person.location].filter(Boolean).join(" • ")}</p>
                  </div>
                </div>
              </button>
            ))}
          </div>
        </section>
      </div>

      <aside className="right-rail">
        <section className="sidebar-card">
          <p className="sidebar-heading">Inbox rhythm</p>
          <p className="sidebar-text">
            Keep messages focused and useful. Good outreach usually references a shared context, role, or recent update.
          </p>
        </section>
      </aside>
    </section>
  );
}
