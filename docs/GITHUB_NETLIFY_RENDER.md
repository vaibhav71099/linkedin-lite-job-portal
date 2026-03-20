# GitHub, Netlify, Render Setup

This project is now prepared for:

- GitHub source control
- Netlify frontend deployment
- Render backend deployment

## 1. Push to GitHub

First set your git identity:

```bash
git config --global user.name "Your Name"
git config --global user.email "your-email@example.com"
```

Then from the project root:

```bash
cd /Users/vaibhavsuryawanshi/Downloads/jobportal
git add .
git commit -m "Initial LinkedIn Lite project"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git push -u origin main
```

## 2. Deploy Frontend on Netlify

Netlify will automatically use [netlify.toml](/Users/vaibhavsuryawanshi/Downloads/jobportal/netlify.toml).

Required environment variable:

```env
VITE_API_BASE_URL=https://your-backend-url.onrender.com
```

Important:

- frontend build base is `frontend`
- SPA routes are handled with:
  - [netlify.toml](/Users/vaibhavsuryawanshi/Downloads/jobportal/netlify.toml)
  - [frontend/public/_redirects](/Users/vaibhavsuryawanshi/Downloads/jobportal/frontend/public/_redirects)

## 3. Deploy Backend on Render

Render will use [render.yaml](/Users/vaibhavsuryawanshi/Downloads/jobportal/render.yaml).

Set these backend environment variables in Render:

```env
DB_USERNAME=root
DB_PASSWORD=your-db-password
SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/jobportal?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
JWT_SECRET=your-strong-secret
JWT_EXPIRATION=86400000
APP_CORS_ALLOWED_ORIGINS=https://your-netlify-site.netlify.app
SERVER_PORT=8081
```

## 4. Recommended Order

1. Push project to GitHub
2. Deploy backend on Render
3. Copy backend public URL
4. Set `VITE_API_BASE_URL` on Netlify
5. Deploy frontend on Netlify
