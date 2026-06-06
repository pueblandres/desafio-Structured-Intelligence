// This API does not use a service worker. The file exists to satisfy browsers
// that retry a previously registered service worker from localhost.
self.addEventListener("install", () => {
    self.skipWaiting();
});

self.addEventListener("activate", (event) => {
    event.waitUntil(self.registration.unregister());
});
