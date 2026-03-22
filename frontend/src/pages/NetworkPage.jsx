import { useEffect, useState } from "react";
import api from "../api";

function PersonCard({ person, actionLabel, onAction, actionDisabled = false }) {
  const skills = (person.skills || "")
    .split(",")
    .map((skill) => skill.trim())
    .filter(Boolean)
    .slice(0, 3);

  return (
    <article className="network-card">
      <div className="network-card-top">
        <div className="profile-avatar">{(person.name || "U").slice(0, 1).toUpperCase()}</div>
        <div>
          <h3>{person.name}</h3>
          <p className="network-headline">{person.headline || "Professional profile"}</p>
          <p className="sidebar-text">
            {[person.currentCompany, person.location].filter(Boolean).join(" • ") || "Building a stronger professional presence"}
          </p>
        </div>
      </div>

      {skills.length > 0 && (
        <div className="network-skill-list">
          {skills.map((skill) => (
            <span key={skill} className="soft-pill">{skill}</span>
          ))}
        </div>
      )}

      {onAction && (
        <button
          type="button"
          className="ghost-button inline-button"
          disabled={actionDisabled}
          onClick={onAction}
        >
          {actionLabel}
        </button>
      )}
    </article>
  );
}

export default function NetworkPage() {
  const [discoverPeople, setDiscoverPeople] = useState([]);
  const [invitations, setInvitations] = useState([]);
  const [connections, setConnections] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);

  useEffect(() => {
    loadNetwork();
  }, []);

  async function loadNetwork() {
    setLoading(true);
    try {
      const [discoverResponse, invitationsResponse, connectionsResponse] = await Promise.all([
        api.get("/api/network/discover"),
        api.get("/api/network/invitations"),
        api.get("/api/network/connections")
      ]);
      setDiscoverPeople(discoverResponse.data.data);
      setInvitations(invitationsResponse.data.data);
      setConnections(connectionsResponse.data.data);
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load your network.");
    } finally {
      setLoading(false);
    }
  }

  async function handleConnect(userId) {
    setBusyId(userId);
    setMessage("");
    try {
      await api.post(`/api/network/requests/${userId}`);
      setMessage("Invitation sent.");
      await loadNetwork();
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to send invitation.");
    } finally {
      setBusyId(null);
    }
  }

  async function handleInvitation(requestId, action) {
    setBusyId(requestId);
    setMessage("");
    try {
      await api.post(`/api/network/requests/${requestId}/${action}`);
      setMessage(action === "accept" ? "Invitation accepted." : "Invitation ignored.");
      await loadNetwork();
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to update invitation.");
    } finally {
      setBusyId(null);
    }
  }

  return (
    <section className="page-section">
      <div className="content-feed">
        <section className="feed-card">
          <div className="section-heading">
            <div>
              <p className="eyebrow">My Network</p>
              <h2>Grow a credible professional circle</h2>
              <p className="support-text">
                Discover people, manage invitations, and build a network that makes your profile feel alive.
              </p>
            </div>
          </div>

          {message && <p className={`message ${message.includes("Unable") ? "error" : "success"}`}>{message}</p>}

          {!loading && (
            <div className="network-overview-grid">
              <div className="mini-stat">
                <span>Connections</span>
                <strong>{connections.length}</strong>
              </div>
              <div className="mini-stat">
                <span>Pending invites</span>
                <strong>{invitations.length}</strong>
              </div>
              <div className="mini-stat">
                <span>People to discover</span>
                <strong>{discoverPeople.length}</strong>
              </div>
            </div>
          )}
        </section>

        <section className="feed-card">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Invitations</p>
              <h3>People who want to connect</h3>
            </div>
          </div>

          {loading && <p className="support-text">Loading invitations...</p>}
          {!loading && invitations.length === 0 && (
            <div className="empty-state">
              <strong>No pending invitations</strong>
              <p className="support-text">Incoming requests will show up here.</p>
            </div>
          )}

          <div className="network-grid">
            {invitations.map((invitation) => (
              <article key={invitation.id} className="network-card">
                <div className="network-card-top">
                  <div className="profile-avatar">{(invitation.requester.name || "U").slice(0, 1).toUpperCase()}</div>
                  <div>
                    <h3>{invitation.requester.name}</h3>
                    <p className="network-headline">{invitation.requester.headline || "Professional profile"}</p>
                    <p className="sidebar-text">
                      {[invitation.requester.currentCompany, invitation.requester.location].filter(Boolean).join(" • ") || "Professional invitation"}
                    </p>
                  </div>
                </div>

                <div className="network-actions">
                  <button
                    type="button"
                    className="primary-button inline-button"
                    disabled={busyId === invitation.id}
                    onClick={() => handleInvitation(invitation.id, "accept")}
                  >
                    Accept
                  </button>
                  <button
                    type="button"
                    className="ghost-button inline-button"
                    disabled={busyId === invitation.id}
                    onClick={() => handleInvitation(invitation.id, "ignore")}
                  >
                    Ignore
                  </button>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="feed-card">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Discover</p>
              <h3>People you may want to know</h3>
            </div>
          </div>

          {!loading && discoverPeople.length === 0 && (
            <div className="empty-state">
              <strong>No suggestions right now</strong>
              <p className="support-text">As more people join, recommendations will appear here.</p>
            </div>
          )}

          <div className="network-grid">
            {discoverPeople.map((person) => (
              <PersonCard
                key={person.id}
                person={person}
                actionLabel="Connect"
                actionDisabled={busyId === person.id}
                onAction={() => handleConnect(person.id)}
              />
            ))}
          </div>
        </section>
      </div>

      <aside className="right-rail">
        <section className="sidebar-card">
          <p className="sidebar-heading">Connection strategy</p>
          <p className="sidebar-text">
            Profiles with a clear headline, company, location, and skills get more meaningful connections.
          </p>
        </section>

        <section className="sidebar-card">
          <p className="sidebar-heading">Your circle</p>
          <div className="network-mini-list">
            {connections.slice(0, 5).map((person) => (
              <div key={person.id} className="network-mini-item">
                <div className="profile-mini-avatar">{(person.name || "U").slice(0, 1).toUpperCase()}</div>
                <div>
                  <strong>{person.name}</strong>
                  <p>{person.headline || "Professional contact"}</p>
                </div>
              </div>
            ))}
            {!loading && connections.length === 0 && (
              <p className="support-text">Start adding connections to build your graph.</p>
            )}
          </div>
        </section>
      </aside>
    </section>
  );
}
