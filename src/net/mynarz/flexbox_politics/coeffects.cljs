(ns net.mynarz.flexbox-politics.coeffects
  (:require [re-frame.core :as rf]))

(rf/reg-cofx
  ::bounding-box
  (fn [cofx {:keys [id]}]
    (let [bounding-box (-> js/document
                           (.getElementById id)
                           (.getBoundingClientRect))]
      (assoc-in cofx [::bounding-box id] bounding-box))))
