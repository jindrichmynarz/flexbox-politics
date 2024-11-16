(ns net.mynarz.flexbox-politics.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  ::css
  (fn [{:keys [css]}]
    css))

(reg-sub
  ::parsed-css
  (fn [{:keys [parsed-css]}]
    parsed-css))

(reg-sub
  ::political-parties
  (fn [{:keys [political-parties]}]
    political-parties))

(reg-sub
  ::has-won?
  (fn [{:keys [has-won?]}]
    has-won?))
