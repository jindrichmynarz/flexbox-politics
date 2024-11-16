(ns net.mynarz.flexbox-politics.views
  (:require ["canvas-confetti" :as confetti]
            [goog.string :as gstring]
            [goog.string.format]
            [net.mynarz.flexbox-politics.events :as events]
            [net.mynarz.flexbox-politics.subscriptions :as subs]
            ["react" :as react]
            [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn header
  []
  [:h1 "Flexbox politics"])

(defn mission
  []
  [:<>
    [:h2 "Politická strategie"]
    [:p "Piráti potřebují vycentrovat!"]])

(defn editor
  []
  (let [css @(rf/subscribe [::subs/css])]
    [:div#editor
     [:pre "#pirati {"]
     [:textarea#code
      {:auto-capitalize "none"
       :auto-focus "autofocus"
       :on-change #(rf/dispatch [::events/update-css (.. % -target -value)])
       :spell-check "false"
       :value css}]
     [:pre "}"]]))

(defn footer
  []
  [:footer
   [:p "Vyrobil "
     [:a {:href "https://mynarz.net/#jindrich"} "Jindřich Mynarz"]
     " s inspirací od "
     [:a {:href "https://flexboxfroggy.com"} "Flexbox Froggy"] " 🐸"]])

(defn victory
  []
  [:p#victory "Totální centrismus," [:br] "kterého nedosáhla ani KDU-ČSL!"])

(defn confetti
  []
  (let [element-ref (react/createRef)]
    (reagent/create-class
      {:display-name "confetti"
       :reagent-render (fn [] [:canvas {:id "confetti"
                                        :ref element-ref}])
       :component-did-mount (fn [_]
                              (let [celebrate (confetti/create (.-current element-ref) #js {:resize true})]
                                (celebrate #js {:particleCount 100 :spread 160})))})))

(defn css-editor
  []
  (let [has-won? @(rf/subscribe [::subs/has-won?])]
    [:section#css-editor
     [header]
     [mission]
     [editor]
     [footer]
     (when has-won?
       [:<>
        [victory]
        [confetti]])]))

(def target
  [:div#target])

(def labels
  [:div#labels
   [:div "Autoritářství"]
   [:div "Pravice"]
   [:div "Libertariánství"]
   [:div "Levice"]])

(def quadrants
  [:div#quadrants
   [:div#tl.quadrant]
   [:div#tr.quadrant]
   [:div#bl.quadrant]
   [:div#br.quadrant]])

(defn parties
  []
  (let [political-parties @(rf/subscribe [::subs/political-parties])
        parsed-css @(rf/subscribe [::subs/parsed-css])]
    [:div#parties
     (for [{:keys [id title]} political-parties]
       ^{:key id}
       [:div
        (cond-> {:id id}
          (and parsed-css (= id "pirati")) (assoc :style parsed-css))
        [:img {:src (gstring/format "img/%s.svg" id)
               :title title}]])]))

(defn political-compass
  []
  [:div#political-compass
   target
   labels
   quadrants
   [parties]])

(defn view
  []
  [:section#view
    [political-compass]])

(defn ui
  []
  [:<>
   [css-editor]
   [view]])
