(ns clweb.form-util
  (:require
  [com.rpl.specter :as s]
   #?(:cljs [reagent-forms.core :refer [bind-fields]])))

#?(:clj
   (def bind-fields 'bind-fields))

(defn field [type label state path]
  (let [error (get-in @state (conj path :error))]
    [:div.field {:class (when (some? error) "error")}
     [:label label]
     [bind-fields [:input {:field type :id (conj path :value)}] state]
     (when (some? error) [:div.ui.pointing.red.basic.label error])]))

(defn errors [validation]
  (filter
   #(not (nil? %))
   (s/select [s/ALL s/LAST s/MAP-VALS :error] validation)))
