const VERSION = 'pocketflow-shell-v1';
const BASE = self.registration.scope;
const SHELL = [
  BASE,
  new URL('./index.html', BASE).href,
  new URL('./manifest.webmanifest', BASE).href,
  new URL('./icon.svg', BASE).href,
  new URL('./icon-192.png', BASE).href,
  new URL('./icon-512.png', BASE).href
];

self.addEventListener('install', event => {
  event.waitUntil(caches.open(VERSION).then(cache => cache.addAll(SHELL)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', event => {
  event.waitUntil((async () => {
    const keys = await caches.keys();
    await Promise.all(keys.filter(key => key.startsWith('pocketflow-shell-') && key !== VERSION).map(key => caches.delete(key)));
    await self.clients.claim();
  })());
});

self.addEventListener('fetch', event => {
  const request = event.request;
  const url = new URL(request.url);
  if (request.method !== 'GET' || url.origin !== self.location.origin || !url.pathname.startsWith(new URL(BASE).pathname)) return;

  event.respondWith((async () => {
    const cache = await caches.open(VERSION);
    try {
      const response = await fetch(request);
      if (response.ok) cache.put(request, response.clone());
      return response;
    } catch {
      const cached = await cache.match(request);
      if (cached) return cached;
      if (request.mode === 'navigate') return (await cache.match(new URL('./index.html', BASE).href)) || Response.error();
      return Response.error();
    }
  })());
});
