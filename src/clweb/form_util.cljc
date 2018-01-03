(ns clweb.form-util
  #?(:cljs (:require [reagent-forms.core :refer [bind-fields]])))

#?(:clj
   (def bind-fields 'bind-fields))

(defn field [type label state path]
  (let [error (get-in @state (conj path :error))]
    [:div.field {:class (when (some? error) "error")}
     [:label label]
     [bind-fields [:input {:field type :id (conj path :value)}] state]
     (when (some? error) [:div.ui.pointing.red.basic.label error])]))
