import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api";
import { getUser, saveUser } from "../utils/auth";

function formatRelativeTime(value) {
  if (!value) {
    return "Just now";
  }

  const createdAt = new Date(value).getTime();
  const diffMinutes = Math.max(1, Math.floor((Date.now() - createdAt) / 60000));

  if (diffMinutes < 60) {
    return `${diffMinutes}m`;
  }

  const diffHours = Math.floor(diffMinutes / 60);
  if (diffHours < 24) {
    return `${diffHours}h`;
  }

  const diffDays = Math.floor(diffHours / 24);
  if (diffDays < 7) {
    return `${diffDays}d`;
  }

  return new Date(value).toLocaleDateString();
}

export default function DashboardPage() {
  const [profile, setProfile] = useState(getUser());
  const [applications, setApplications] = useState([]);
  const [myJobs, setMyJobs] = useState([]);
  const [connections, setConnections] = useState([]);
  const [invitations, setInvitations] = useState([]);
  const [posts, setPosts] = useState([]);
  const [composer, setComposer] = useState({ content: "", imageUrl: "" });
  const [commentDrafts, setCommentDrafts] = useState({});
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [posting, setPosting] = useState(false);
  const [busyPostId, setBusyPostId] = useState(null);

  useEffect(() => {
    loadDashboard();
  }, []);

  async function loadDashboard() {
    try {
      const profileResponse = await api.get("/api/users/me");
      const nextProfile = profileResponse.data.data;
      setProfile(nextProfile);
      saveUser(nextProfile);

      const requests = [
        api.get("/api/network/connections"),
        api.get("/api/network/invitations"),
        api.get("/api/posts")
      ];

      if (nextProfile.role === "USER") {
        requests.push(api.get("/api/applications/my"));
      }

      if (nextProfile.role === "RECRUITER") {
        requests.push(api.get("/jobs/mine"));
      }

      const responses = await Promise.all(requests);
      setConnections(responses[0].data.data);
      setInvitations(responses[1].data.data);
      setPosts(responses[2].data.data);

      if (nextProfile.role === "USER") {
        setApplications(responses[3]?.data?.data || []);
      }

      if (nextProfile.role === "RECRUITER") {
        const recruiterIndex = nextProfile.role === "RECRUITER" ? 3 : 4;
        setMyJobs(responses[recruiterIndex]?.data?.data || []);
      }
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load the home feed.");
    } finally {
      setLoading(false);
    }
  }

  async function handleCreatePost(event) {
    event.preventDefault();
    setPosting(true);
    setMessage("");

    try {
      await api.post("/api/posts", composer);
      setComposer({ content: "", imageUrl: "" });
      await loadDashboard();
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to create post.");
    } finally {
      setPosting(false);
    }
  }

  async function handleReact(postId) {
    setBusyPostId(postId);
    setMessage("");
    try {
      const response = await api.post(`/api/posts/${postId}/react`);
      setPosts((currentPosts) =>
        currentPosts.map((post) => (post.id === postId ? response.data.data : post))
      );
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to update reaction.");
    } finally {
      setBusyPostId(null);
    }
  }

  async function handleComment(postId) {
    const draft = (commentDrafts[postId] || "").trim();
    if (!draft) {
      return;
    }

    setBusyPostId(postId);
    setMessage("");

    try {
      await api.post(`/api/posts/${postId}/comments`, { content: draft });
      setCommentDrafts((current) => ({ ...current, [postId]: "" }));
      await refreshFeedOnly();
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to add comment.");
    } finally {
      setBusyPostId(null);
    }
  }

  async function refreshFeedOnly() {
    const response = await api.get("/api/posts");
    setPosts(response.data.data);
  }

  return (
    <section className="page-section row g-4">
      <div className="content-feed col-12 col-xl-8">
        <section className="feed-card hero-card">
          <div className="hero-copy">
            <p className="eyebrow">Professional home</p>
            <h2>
              {profile?.headline || "Build visibility with a profile, network, and activity stream that feels real"}
            </h2>
            <p className="support-text">
              Share updates, react to your network, and keep your professional identity active instead of static.
            </p>

            <div className="hero-actions">
              <Link to="/profile" className="btn btn-primary btn-sm">
                Edit profile
              </Link>
              <Link to="/network" className="btn btn-outline-primary btn-sm">
                Grow network
              </Link>
            </div>
          </div>

          <div className="hero-metrics">
            <div className="metric-tile">
              <span>Connections</span>
              <strong>{connections.length}</strong>
            </div>
            <div className="metric-tile">
              <span>Pending invites</span>
              <strong>{invitations.length}</strong>
            </div>
            <div className="metric-tile">
              <span>Posts in your feed</span>
              <strong>{posts.length}</strong>
            </div>
          </div>
        </section>

        <section className="feed-card composer-card">
          <div className="composer-header">
            <div className="profile-avatar">{(profile?.name || "U").slice(0, 1).toUpperCase()}</div>
            <div>
              <h3>{profile?.name || "Professional"}</h3>
              <p className="sidebar-text">{profile?.headline || "Share a professional update with your network."}</p>
            </div>
          </div>

          <form className="form-grid d-grid gap-3" onSubmit={handleCreatePost}>
            <label>
              <span className="form-label">Start a post</span>
              <textarea
                rows="4"
                className="form-control"
                value={composer.content}
                onChange={(event) => setComposer({ ...composer, content: event.target.value })}
                placeholder="Share a milestone, hiring update, product launch, or lesson learned."
                required
              />
            </label>

            <label>
              <span className="form-label">Image URL</span>
              <input
                type="url"
                className="form-control"
                value={composer.imageUrl}
                onChange={(event) => setComposer({ ...composer, imageUrl: event.target.value })}
                placeholder="Optional image URL for richer posts"
              />
            </label>

            <button type="submit" className="btn btn-primary" disabled={posting}>
              {posting ? "Posting..." : "Post"}
            </button>
          </form>
        </section>

        {message && <p className="message error">{message}</p>}
        {loading && <p className="support-text">Loading home feed...</p>}

        {!loading && posts.length === 0 && (
          <section className="feed-card empty-card">
            <strong>No posts yet</strong>
            <p className="support-text">
              Create the first update or connect with more people so your home feed starts moving.
            </p>
          </section>
        )}

        {!loading &&
          posts.map((post) => (
            <section key={post.id} className="feed-card social-post-card">
              <div className="social-post-header">
                <div className="profile-avatar">{(post.author.name || "U").slice(0, 1).toUpperCase()}</div>
                <div>
                  <strong>{post.author.name}</strong>
                  <p className="sidebar-text">
                    {post.author.headline || "Professional member"} • {formatRelativeTime(post.createdAt)}
                  </p>
                </div>
              </div>

              <p className="social-post-copy">{post.content}</p>

              {post.imageUrl && (
                <div className="social-post-media">
                  <img src={post.imageUrl} alt="Post attachment" />
                </div>
              )}

              <div className="social-post-stats">
                <span>{post.reactionCount} reactions</span>
                <span>{post.commentCount} comments</span>
              </div>

              <div className="social-post-actions">
                <button
                  type="button"
                  className={`btn btn-outline-secondary btn-sm ${post.reactedByCurrentUser ? "active-action" : ""}`}
                  disabled={busyPostId === post.id}
                  onClick={() => handleReact(post.id)}
                >
                  {post.reactedByCurrentUser ? "Reacted" : "React"}
                </button>
              </div>

              <div className="comment-list">
                {post.comments.map((comment) => (
                  <article key={comment.id} className="comment-row">
                    <div className="profile-mini-avatar">
                      {(comment.author.name || "U").slice(0, 1).toUpperCase()}
                    </div>
                    <div className="comment-bubble">
                      <strong>{comment.author.name}</strong>
                      <p>{comment.content}</p>
                    </div>
                  </article>
                ))}
              </div>

              <div className="comment-composer d-flex gap-2">
                <input
                  type="text"
                  className="form-control"
                  value={commentDrafts[post.id] || ""}
                  onChange={(event) =>
                    setCommentDrafts((current) => ({ ...current, [post.id]: event.target.value }))
                  }
                  placeholder="Add a thoughtful comment"
                />
                <button
                  type="button"
                  className="btn btn-outline-secondary btn-sm"
                  disabled={busyPostId === post.id}
                  onClick={() => handleComment(post.id)}
                >
                  Comment
                </button>
              </div>
            </section>
          ))}
      </div>

      <aside className="right-rail col-12 col-xl-4">
        <section className="sidebar-card">
          <p className="sidebar-heading">Profile signal</p>
          <div className="mini-stat-grid">
            <div className="mini-stat">
              <span>Headline</span>
              <strong>{profile?.headline ? "Set" : "Missing"}</strong>
            </div>
            <div className="mini-stat">
              <span>About</span>
              <strong>{profile?.bio ? "Set" : "Missing"}</strong>
            </div>
          </div>
        </section>

        <section className="sidebar-card">
          <p className="sidebar-heading">Career pulse</p>
          <div className="mini-stat-grid">
            <div className="mini-stat">
              <span>{profile?.role === "RECRUITER" ? "Live roles" : "Applications"}</span>
              <strong>{profile?.role === "RECRUITER" ? myJobs.length : applications.length}</strong>
            </div>
            <div className="mini-stat">
              <span>Network</span>
              <strong>{connections.length}</strong>
            </div>
          </div>
        </section>

        <section className="sidebar-card">
          <p className="sidebar-heading">Posting ideas</p>
          <div className="showcase-list">
            <span>Milestone</span>
            <span>Hiring update</span>
            <span>Project launch</span>
            <span>Lesson learned</span>
          </div>
        </section>
      </aside>
    </section>
  );
}
