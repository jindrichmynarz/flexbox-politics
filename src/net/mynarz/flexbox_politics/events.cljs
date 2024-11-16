(ns net.mynarz.flexbox-politics.events
  (:require [clojure.string :as string]
            [net.mynarz.flexbox-politics.coeffects :as cofx]
            [re-frame.core :as rf]))

(def political-parties
  [{:id "ano"
    :title "ANO 2011"}
   {:id "kducsl"
    :title "KDU-ČSL"}
   {:id "kscm"
    :title "Komunistická strana Čech a Moravy"}
   {:id "motoriste"
    :title "Motoristé sobě"}
   {:id "ods"
    :title "Občanská demokratická strana"}
   {:id "pirati"
    :title "Piráti"}
   {:id "prisaha"
    :title "Přísaha"}
   {:id "socdem"
    :title "Sociální demokracie"}
   {:id "spd"
    :title "Svoboda a přímá demokracie"}
   {:id "stan"
    :title "Starostové a nezávislí"}
   {:id "top09"
    :title "TOP 09"}
   {:id "zeleni"
    :title "Strana zelených"}])

(defn long-str
  [strings]
  (string/join "\n" strings))

; (->> political-parties
;                (mapcat (fn [{:keys [id]}] [(str "#" id " {") "}" ""]))
;                long-str)

(defn pair?
  [pair]
  (when (= (count pair) 2)
    pair))

(defn parse-css-kv
  [css]
  (when-not (string/blank? css)
    (-> css
        string/trim
        (string/replace #";$" "")
        (string/split #":\s*" 2)
        pair?)))

(defn parse-css
  [css]
  (some->> css
           string/split-lines
           (map parse-css-kv)
           (into {})))

(rf/reg-event-db
  ::initialize
  (fn [_]
    {:css ""
     :political-parties (shuffle political-parties)}))

(defn is-contained?
  [target container]
  (and (>= (.-left target) (.-left container))
       (<= (+ (.-left target) (.-width target))
           (+ (.-left container) (.-width container)))
       (>= (.-top target) (.-top container))
       (<= (+ (.-top target) (.-height target))
           (+ (.-top container) (.-height container)))))

(rf/reg-event-fx
  ::has-won?
  [(rf/inject-cofx ::cofx/bounding-box {:id "pirati"})
   (rf/inject-cofx ::cofx/bounding-box {:id "target"})]
  (fn [{::cofx/keys [bounding-box]
        :keys [db]}]
    (let [has-won? (is-contained? (get bounding-box "pirati") (get bounding-box "target"))]
      {:db (assoc db :has-won? has-won?)})))

(rf/reg-event-fx
  ::update-css
  (fn [{:keys [db]} [_ css]]
    (let [parsed-css (parse-css css)]
      {:db (cond-> (assoc db :css css)
             parsed-css (assoc :parsed-css parsed-css))
       :fx [[:dispatch [::has-won?]]]})))
