(ns net.mynarz.flexbox-politics.core
  (:require [net.mynarz.flexbox-politics.events :as events]
            ; Must load subscriptions, so that they are not elided by the compiler.
            [net.mynarz.flexbox-politics.subscriptions]
            [net.mynarz.flexbox-politics.views :as views]
            ["react" :as react]
            [reagent.core :as reagent]
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf]))

(defn dev-setup
  []
  (when goog.DEBUG
    (enable-console-print!) ; so that println writes to `console.log`
    (println "dev mode")))

(defonce root
  ;; Init only on use, this ns is loaded for SSR build also
  (delay (rdc/create-root (js/document.getElementById "app"))))

(defn run
  []
  (dev-setup)
  (when reagent/is-client
    ;; Enable StrictMode to warn about e.g. findDOMNode
    (rdc/render @root [:> react/StrictMode {} [views/ui]])))

;; The `:dev/after-load` metadata causes this function to be called
;; after shadow-cljs hot-reloads code. We force a UI update by clearing
;; the Reframe subscription cache.
(defn ^:dev/after-load on-reload
  []
  (rf/clear-subscription-cache!)
  (run))

(defn ^:export main
  []
  (rf/dispatch-sync [::events/initialize])
  (run))
