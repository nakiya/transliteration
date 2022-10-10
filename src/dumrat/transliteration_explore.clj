(ns dumrat.transliteration-explore
  (:gen-class)
  (:require [clojure.string :as str]
            [instaparse.core :as insta]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args])

(def vowel-info
  {"a" "අ"
   "aa" "ආ"
   "A" "ඇ"
   "AA" "ඈ"
   "i" "ඉ"
   "ii" "ඊ"
   "ee" "ඊ"
   "u" "උ"
   "uu" "ඌ"
   "sru" "ඍ"
   "sruu" "ඎ"
   "sroo" "ඎ"
   "e" "එ"
   "ea" "ඒ"
   "I" "ඓ"
   "o" "ඔ"
   "O" "ඕ"
   "au" "ඖ"
   "ou" "ඖ"})

(defn transliterate-vowel [text]
  (if-let [tr (vowel-info text)]
    tr
    ""))

(transliterate-vowel "O")
(transliterate-vowel "OO")

(def vowel-info-raw "U+0D85	අ	224 182 133	SINHALA LETTER AYANNA	
U+0D86	ආ	224 182 134	SINHALA LETTER AAYANNA	
U+0D87	ඇ	224 182 135	SINHALA LETTER AEYANNA	
U+0D88	ඈ	224 182 136	SINHALA LETTER AEEYANNA	
U+0D89	ඉ	224 182 137	SINHALA LETTER IYANNA	
U+0D8A	ඊ	224 182 138	SINHALA LETTER IIYANNA	
U+0D8B	උ	224 182 139	SINHALA LETTER UYANNA	
U+0D8C	ඌ	224 182 140	SINHALA LETTER UUYANNA	
U+0D8D	ඍ	224 182 141	SINHALA LETTER IRUYANNA	
U+0D8E	ඎ	224 182 142	SINHALA LETTER IRUUYANNA	
U+0D8F	ඏ	224 182 143	SINHALA LETTER ILUYANNA	
U+0D90	ඐ	224 182 144	SINHALA LETTER ILUUYANNA	
U+0D91	එ	224 182 145	SINHALA LETTER EYANNA	
U+0D92	ඒ	224 182 146	SINHALA LETTER EEYANNA	
U+0D93	ඓ	224 182 147	SINHALA LETTER AIYANNA	
U+0D94	ඔ	224 182 148	SINHALA LETTER OYANNA	
U+0D95	ඕ	224 182 149	SINHALA LETTER OOYANNA	
U+0D96	ඖ	224 182 150	SINHALA LETTER AUYANNA")

; Let's Split by line and then column.

; First let's get an alias to clojure.string. This makes for less typing.
(require '[clojure.string :as str])

(defn extract-info [info-raw]
  (as-> info-raw th
    (str/split th #"\n")
    (map (fn [l] (str/split l #"\t")) th)
    ;Let's convert first to Clojure unicode
    (map (fn [l] (assoc l 0 (str "\\" "u" (subs (l 0) 2)))) th)
    (mapv println th)))

(extract-info vowel-info-raw)

; prints out below:
;; [\u0D85 අ 224 182 133 SINHALA LETTER AYANNA]
;; [\u0D86 ආ 224 182 134 SINHALA LETTER AAYANNA]
;; [\u0D87 ඇ 224 182 135 SINHALA LETTER AEYANNA]
;; [\u0D88 ඈ 224 182 136 SINHALA LETTER AEEYANNA]
;; [\u0D89 ඉ 224 182 137 SINHALA LETTER IYANNA]
;; [\u0D8A ඊ 224 182 138 SINHALA LETTER IIYANNA]
;; [\u0D8B උ 224 182 139 SINHALA LETTER UYANNA]
;; [\u0D8C ඌ 224 182 140 SINHALA LETTER UUYANNA]
;; [\u0D8D ඍ 224 182 141 SINHALA LETTER IRUYANNA]
;; [\u0D8E ඎ 224 182 142 SINHALA LETTER IRUUYANNA]
;; [\u0D8F ඏ 224 182 143 SINHALA LETTER ILUYANNA]
;; [\u0D90 ඐ 224 182 144 SINHALA LETTER ILUUYANNA]
;; [\u0D91 එ 224 182 145 SINHALA LETTER EYANNA]
;; [\u0D92 ඒ 224 182 146 SINHALA LETTER EEYANNA]
;; [\u0D93 ඓ 224 182 147 SINHALA LETTER AIYANNA]
;; [\u0D94 ඔ 224 182 148 SINHALA LETTER OYANNA]
;; [\u0D95 ඕ 224 182 149 SINHALA LETTER OOYANNA]
;; [\u0D96 ඖ 224 182 150 SINHALA LETTER AUYANNA]

; extract-info again
(defn extract-info [info-raw]
  (as-> info-raw th
    (str/split th #"\n")
    (map (fn [l] (str/split l #"\t")) th)
    ;Let's convert first to Clojure unicode
    (map (fn [l] (assoc l 0 (str "\\" "u" (subs (l 0) 2)))) th)
    (map (fn [l] (mapv (fn [e] (str "\"" e "\"")) l)) th)
    ; Interleave and get a map
    (map (fn [l] (into [] (interleave [:code-point :character :utf-8 :description] l))) th)
    (map (fn [l] (into [] (map (fn [e] (into [] e)) (partition 2 l)))) th)
    (map (fn [l] (into {} l)) th)
    ; This is to remove empty description lines. Will be useful in future.
    (filter :description th)
    (mapv println th)))

(extract-info vowel-info-raw)

; After all this gymnastics we get something like this

;; {:code-point "\u0D85", :character "අ", :utf-8 "224 182 133", :description "SINHALA LETTER AYANNA"}
;; {:code-point "\u0D86", :character "ආ", :utf-8 "224 182 134", :description "SINHALA LETTER AAYANNA"}
;; {:code-point "\u0D87", :character "ඇ", :utf-8 "224 182 135", :description "SINHALA LETTER AEYANNA"}
;; {:code-point "\u0D88", :character "ඈ", :utf-8 "224 182 136", :description "SINHALA LETTER AEEYANNA"}
;; {:code-point "\u0D89", :character "ඉ", :utf-8 "224 182 137", :description "SINHALA LETTER IYANNA"}
;; {:code-point "\u0D8A", :character "ඊ", :utf-8 "224 182 138", :description "SINHALA LETTER IIYANNA"}
;; {:code-point "\u0D8B", :character "උ", :utf-8 "224 182 139", :description "SINHALA LETTER UYANNA"}
;; {:code-point "\u0D8C", :character "ඌ", :utf-8 "224 182 140", :description "SINHALA LETTER UUYANNA"}
;; {:code-point "\u0D8D", :character "ඍ", :utf-8 "224 182 141", :description "SINHALA LETTER IRUYANNA"}
;; {:code-point "\u0D8E", :character "ඎ", :utf-8 "224 182 142", :description "SINHALA LETTER IRUUYANNA"}
;; {:code-point "\u0D8F", :character "ඏ", :utf-8 "224 182 143", :description "SINHALA LETTER ILUYANNA"}
;; {:code-point "\u0D90", :character "ඐ", :utf-8 "224 182 144", :description "SINHALA LETTER ILUUYANNA"}
;; {:code-point "\u0D91", :character "එ", :utf-8 "224 182 145", :description "SINHALA LETTER EYANNA"}
;; {:code-point "\u0D92", :character "ඒ", :utf-8 "224 182 146", :description "SINHALA LETTER EEYANNA"}
;; {:code-point "\u0D93", :character "ඓ", :utf-8 "224 182 147", :description "SINHALA LETTER AIYANNA"}
;; {:code-point "\u0D94", :character "ඔ", :utf-8 "224 182 148", :description "SINHALA LETTER OYANNA"}
;; {:code-point "\u0D95", :character "ඕ", :utf-8 "224 182 149", :description "SINHALA LETTER OOYANNA"}
;; {:code-point "\u0D96", :character "ඖ", :utf-8 "224 182 150", :description "SINHALA LETTER AUYANNA"}

; Now we just use this as our data.

(def vowel-info [{:code-point "\u0D85", :character "අ", :utf-8 "224 182 133", :description "SINHALA LETTER AYANNA"}
                 {:code-point "\u0D86", :character "ආ", :utf-8 "224 182 134", :description "SINHALA LETTER AAYANNA"}
                 {:code-point "\u0D87", :character "ඇ", :utf-8 "224 182 135", :description "SINHALA LETTER AEYANNA"}
                 {:code-point "\u0D88", :character "ඈ", :utf-8 "224 182 136", :description "SINHALA LETTER AEEYANNA"}
                 {:code-point "\u0D89", :character "ඉ", :utf-8 "224 182 137", :description "SINHALA LETTER IYANNA"}
                 {:code-point "\u0D8A", :character "ඊ", :utf-8 "224 182 138", :description "SINHALA LETTER IIYANNA"}
                 {:code-point "\u0D8B", :character "උ", :utf-8 "224 182 139", :description "SINHALA LETTER UYANNA"}
                 {:code-point "\u0D8C", :character "ඌ", :utf-8 "224 182 140", :description "SINHALA LETTER UUYANNA"}
                 {:code-point "\u0D8D", :character "ඍ", :utf-8 "224 182 141", :description "SINHALA LETTER IRUYANNA"}
                 {:code-point "\u0D8E", :character "ඎ", :utf-8 "224 182 142", :description "SINHALA LETTER IRUUYANNA"}
                 {:code-point "\u0D8F", :character "ඏ", :utf-8 "224 182 143", :description "SINHALA LETTER ILUYANNA"}
                 {:code-point "\u0D90", :character "ඐ", :utf-8 "224 182 144", :description "SINHALA LETTER ILUUYANNA"}
                 {:code-point "\u0D91", :character "එ", :utf-8 "224 182 145", :description "SINHALA LETTER EYANNA"}
                 {:code-point "\u0D92", :character "ඒ", :utf-8 "224 182 146", :description "SINHALA LETTER EEYANNA"}
                 {:code-point "\u0D93", :character "ඓ", :utf-8 "224 182 147", :description "SINHALA LETTER AIYANNA"}
                 {:code-point "\u0D94", :character "ඔ", :utf-8 "224 182 148", :description "SINHALA LETTER OYANNA"}
                 {:code-point "\u0D95", :character "ඕ", :utf-8 "224 182 149", :description "SINHALA LETTER OOYANNA"}
                 {:code-point "\u0D96", :character "ඖ", :utf-8 "224 182 150", :description "SINHALA LETTER AUYANNA"}])

(def sinhala-info-raw
  "U+0D80	඀	224 182 128		
U+0D81	ඁ	224 182 129	SINHALA SIGN CANDRABINDU	
U+0D82	ං	224 182 130	SINHALA SIGN ANUSVARAYA	
U+0D83	ඃ	224 182 131	SINHALA SIGN VISARGAYA	
U+0D84	඄	224 182 132		
U+0D85	අ	224 182 133	SINHALA LETTER AYANNA	
U+0D86	ආ	224 182 134	SINHALA LETTER AAYANNA	
U+0D87	ඇ	224 182 135	SINHALA LETTER AEYANNA	
U+0D88	ඈ	224 182 136	SINHALA LETTER AEEYANNA	
U+0D89	ඉ	224 182 137	SINHALA LETTER IYANNA	
U+0D8A	ඊ	224 182 138	SINHALA LETTER IIYANNA	
U+0D8B	උ	224 182 139	SINHALA LETTER UYANNA	
U+0D8C	ඌ	224 182 140	SINHALA LETTER UUYANNA	
U+0D8D	ඍ	224 182 141	SINHALA LETTER IRUYANNA	
U+0D8E	ඎ	224 182 142	SINHALA LETTER IRUUYANNA	
U+0D8F	ඏ	224 182 143	SINHALA LETTER ILUYANNA	
U+0D90	ඐ	224 182 144	SINHALA LETTER ILUUYANNA	
U+0D91	එ	224 182 145	SINHALA LETTER EYANNA	
U+0D92	ඒ	224 182 146	SINHALA LETTER EEYANNA	
U+0D93	ඓ	224 182 147	SINHALA LETTER AIYANNA	
U+0D94	ඔ	224 182 148	SINHALA LETTER OYANNA	
U+0D95	ඕ	224 182 149	SINHALA LETTER OOYANNA	
U+0D96	ඖ	224 182 150	SINHALA LETTER AUYANNA	
U+0D97	඗	224 182 151		
U+0D98	඘	224 182 152		
U+0D99	඙	224 182 153		
U+0D9A	ක	224 182 154	SINHALA LETTER ALPAPRAANA KAYANNA	
U+0D9B	ඛ	224 182 155	SINHALA LETTER MAHAAPRAANA KAYANNA	
U+0D9C	ග	224 182 156	SINHALA LETTER ALPAPRAANA GAYANNA	
U+0D9D	ඝ	224 182 157	SINHALA LETTER MAHAAPRAANA GAYANNA	
U+0D9E	ඞ	224 182 158	SINHALA LETTER KANTAJA NAASIKYAYA	
U+0D9F	ඟ	224 182 159	SINHALA LETTER SANYAKA GAYANNA	
U+0DA0	ච	224 182 160	SINHALA LETTER ALPAPRAANA CAYANNA	
U+0DA1	ඡ	224 182 161	SINHALA LETTER MAHAAPRAANA CAYANNA	
U+0DA2	ජ	224 182 162	SINHALA LETTER ALPAPRAANA JAYANNA	
U+0DA3	ඣ	224 182 163	SINHALA LETTER MAHAAPRAANA JAYANNA	
U+0DA4	ඤ	224 182 164	SINHALA LETTER TAALUJA NAASIKYAYA	
U+0DA5	ඥ	224 182 165	SINHALA LETTER TAALUJA SANYOOGA NAAKSIKYAYA	
U+0DA6	ඦ	224 182 166	SINHALA LETTER SANYAKA JAYANNA	
U+0DA7	ට	224 182 167	SINHALA LETTER ALPAPRAANA TTAYANNA	
U+0DA8	ඨ	224 182 168	SINHALA LETTER MAHAAPRAANA TTAYANNA	
U+0DA9	ඩ	224 182 169	SINHALA LETTER ALPAPRAANA DDAYANNA	
U+0DAA	ඪ	224 182 170	SINHALA LETTER MAHAAPRAANA DDAYANNA	
U+0DAB	ණ	224 182 171	SINHALA LETTER MUURDHAJA NAYANNA	
U+0DAC	ඬ	224 182 172	SINHALA LETTER SANYAKA DDAYANNA	
U+0DAD	ත	224 182 173	SINHALA LETTER ALPAPRAANA TAYANNA	
U+0DAE	ථ	224 182 174	SINHALA LETTER MAHAAPRAANA TAYANNA	
U+0DAF	ද	224 182 175	SINHALA LETTER ALPAPRAANA DAYANNA	
U+0DB0	ධ	224 182 176	SINHALA LETTER MAHAAPRAANA DAYANNA	
U+0DB1	න	224 182 177	SINHALA LETTER DANTAJA NAYANNA	
U+0DB2	඲	224 182 178		
U+0DB3	ඳ	224 182 179	SINHALA LETTER SANYAKA DAYANNA	
U+0DB4	ප	224 182 180	SINHALA LETTER ALPAPRAANA PAYANNA	
U+0DB5	ඵ	224 182 181	SINHALA LETTER MAHAAPRAANA PAYANNA	
U+0DB6	බ	224 182 182	SINHALA LETTER ALPAPRAANA BAYANNA	
U+0DB7	භ	224 182 183	SINHALA LETTER MAHAAPRAANA BAYANNA	
U+0DB8	ම	224 182 184	SINHALA LETTER MAYANNA	
U+0DB9	ඹ	224 182 185	SINHALA LETTER AMBA BAYANNA	
U+0DBA	ය	224 182 186	SINHALA LETTER YAYANNA	
U+0DBB	ර	224 182 187	SINHALA LETTER RAYANNA	
U+0DBC	඼	224 182 188		
U+0DBD	ල	224 182 189	SINHALA LETTER DANTAJA LAYANNA	
U+0DBE	඾	224 182 190		
U+0DBF	඿	224 182 191		
U+0DC0	ව	224 183 128	SINHALA LETTER VAYANNA	
U+0DC1	ශ	224 183 129	SINHALA LETTER TAALUJA SAYANNA	
U+0DC2	ෂ	224 183 130	SINHALA LETTER MUURDHAJA SAYANNA	
U+0DC3	ස	224 183 131	SINHALA LETTER DANTAJA SAYANNA	
U+0DC4	හ	224 183 132	SINHALA LETTER HAYANNA	
U+0DC5	ළ	224 183 133	SINHALA LETTER MUURDHAJA LAYANNA	
U+0DC6	ෆ	224 183 134	SINHALA LETTER FAYANNA	
U+0DC7	෇	224 183 135		
U+0DC8	෈	224 183 136		
U+0DC9	෉	224 183 137		
U+0DCA	්	224 183 138	SINHALA SIGN AL-LAKUNA	
U+0DCB	෋	224 183 139		
U+0DCC	෌	224 183 140		
U+0DCD	෍	224 183 141		
U+0DCE	෎	224 183 142		
U+0DCF	ා	224 183 143	SINHALA VOWEL SIGN AELA-PILLA	
U+0DD0	ැ	224 183 144	SINHALA VOWEL SIGN KETTI AEDA-PILLA	
U+0DD1	ෑ	224 183 145	SINHALA VOWEL SIGN DIGA AEDA-PILLA	
U+0DD2	ි	224 183 146	SINHALA VOWEL SIGN KETTI IS-PILLA	
U+0DD3	ී	224 183 147	SINHALA VOWEL SIGN DIGA IS-PILLA	
U+0DD4	ු	224 183 148	SINHALA VOWEL SIGN KETTI PAA-PILLA	
U+0DD5	෕	224 183 149		
U+0DD6	ූ	224 183 150	SINHALA VOWEL SIGN DIGA PAA-PILLA	
U+0DD7	෗	224 183 151		
U+0DD8	ෘ	224 183 152	SINHALA VOWEL SIGN GAETTA-PILLA	
U+0DD9	ෙ	224 183 153	SINHALA VOWEL SIGN KOMBUVA	
U+0DDA	ේ	224 183 154	SINHALA VOWEL SIGN DIGA KOMBUVA	
U+0DDB	ෛ	224 183 155	SINHALA VOWEL SIGN KOMBU DEKA	
U+0DDC	ො	224 183 156	SINHALA VOWEL SIGN KOMBUVA HAA AELA-PILLA	
U+0DDD	ෝ	224 183 157	SINHALA VOWEL SIGN KOMBUVA HAA DIGA AELA-PILLA	
U+0DDE	ෞ	224 183 158	SINHALA VOWEL SIGN KOMBUVA HAA GAYANUKITTA	
U+0DDF	ෟ	224 183 159	SINHALA VOWEL SIGN GAYANUKITTA	
U+0DE0	෠	224 183 160		
U+0DE1	෡	224 183 161		
U+0DE2	෢	224 183 162		
U+0DE3	෣	224 183 163		
U+0DE4	෤	224 183 164		
U+0DE5	෥	224 183 165		
U+0DE6	෦	224 183 166	SINHALA LITH DIGIT ZERO	
U+0DE7	෧	224 183 167	SINHALA LITH DIGIT ONE	
U+0DE8	෨	224 183 168	SINHALA LITH DIGIT TWO	
U+0DE9	෩	224 183 169	SINHALA LITH DIGIT THREE	
U+0DEA	෪	224 183 170	SINHALA LITH DIGIT FOUR	
U+0DEB	෫	224 183 171	SINHALA LITH DIGIT FIVE	
U+0DEC	෬	224 183 172	SINHALA LITH DIGIT SIX	
U+0DED	෭	224 183 173	SINHALA LITH DIGIT SEVEN	
U+0DEE	෮	224 183 174	SINHALA LITH DIGIT EIGHT	
U+0DEF	෯	224 183 175	SINHALA LITH DIGIT NINE	
U+0DF0	෰	224 183 176		
U+0DF1	෱	224 183 177		
U+0DF2	ෲ	224 183 178	SINHALA VOWEL SIGN DIGA GAETTA-PILLA	
U+0DF3	ෳ	224 183 179	SINHALA VOWEL SIGN DIGA GAYANUKITTA	
U+0DF4	෴	224 183 180	SINHALA PUNCTUATION KUNDDALIYA	")

(extract-info sinhala-info-raw)

(def sinhala-info
  [{:code-point "\u0D81", :character "ඁ", :utf-8 "224 182 129", :description "SINHALA SIGN CANDRABINDU"}
   {:code-point "\u0D82", :character "ං", :utf-8 "224 182 130", :description "SINHALA SIGN ANUSVARAYA"}
   {:code-point "\u0D83", :character "ඃ", :utf-8 "224 182 131", :description "SINHALA SIGN VISARGAYA"}
   {:code-point "\u0D85", :character "අ", :utf-8 "224 182 133", :description "SINHALA LETTER AYANNA"}
   {:code-point "\u0D86", :character "ආ", :utf-8 "224 182 134", :description "SINHALA LETTER AAYANNA"}
   {:code-point "\u0D87", :character "ඇ", :utf-8 "224 182 135", :description "SINHALA LETTER AEYANNA"}
   {:code-point "\u0D88", :character "ඈ", :utf-8 "224 182 136", :description "SINHALA LETTER AEEYANNA"}
   {:code-point "\u0D89", :character "ඉ", :utf-8 "224 182 137", :description "SINHALA LETTER IYANNA"}
   {:code-point "\u0D8A", :character "ඊ", :utf-8 "224 182 138", :description "SINHALA LETTER IIYANNA"}
   {:code-point "\u0D8B", :character "උ", :utf-8 "224 182 139", :description "SINHALA LETTER UYANNA"}
   {:code-point "\u0D8C", :character "ඌ", :utf-8 "224 182 140", :description "SINHALA LETTER UUYANNA"}
   {:code-point "\u0D8D", :character "ඍ", :utf-8 "224 182 141", :description "SINHALA LETTER IRUYANNA"}
   {:code-point "\u0D8E", :character "ඎ", :utf-8 "224 182 142", :description "SINHALA LETTER IRUUYANNA"}
   {:code-point "\u0D8F", :character "ඏ", :utf-8 "224 182 143", :description "SINHALA LETTER ILUYANNA"}
   {:code-point "\u0D90", :character "ඐ", :utf-8 "224 182 144", :description "SINHALA LETTER ILUUYANNA"}
   {:code-point "\u0D91", :character "එ", :utf-8 "224 182 145", :description "SINHALA LETTER EYANNA"}
   {:code-point "\u0D92", :character "ඒ", :utf-8 "224 182 146", :description "SINHALA LETTER EEYANNA"}
   {:code-point "\u0D93", :character "ඓ", :utf-8 "224 182 147", :description "SINHALA LETTER AIYANNA"}
   {:code-point "\u0D94", :character "ඔ", :utf-8 "224 182 148", :description "SINHALA LETTER OYANNA"}
   {:code-point "\u0D95", :character "ඕ", :utf-8 "224 182 149", :description "SINHALA LETTER OOYANNA"}
   {:code-point "\u0D96", :character "ඖ", :utf-8 "224 182 150", :description "SINHALA LETTER AUYANNA"}
   {:code-point "\u0D9A", :character "ක", :utf-8 "224 182 154", :description "SINHALA LETTER ALPAPRAANA KAYANNA"}
   {:code-point "\u0D9B", :character "ඛ", :utf-8 "224 182 155", :description "SINHALA LETTER MAHAAPRAANA KAYANNA"}
   {:code-point "\u0D9C", :character "ග", :utf-8 "224 182 156", :description "SINHALA LETTER ALPAPRAANA GAYANNA"}
   {:code-point "\u0D9D", :character "ඝ", :utf-8 "224 182 157", :description "SINHALA LETTER MAHAAPRAANA GAYANNA"}
   {:code-point "\u0D9E", :character "ඞ", :utf-8 "224 182 158", :description "SINHALA LETTER KANTAJA NAASIKYAYA"}
   {:code-point "\u0D9F", :character "ඟ", :utf-8 "224 182 159", :description "SINHALA LETTER SANYAKA GAYANNA"}
   {:code-point "\u0DA0", :character "ච", :utf-8 "224 182 160", :description "SINHALA LETTER ALPAPRAANA CAYANNA"}
   {:code-point "\u0DA1", :character "ඡ", :utf-8 "224 182 161", :description "SINHALA LETTER MAHAAPRAANA CAYANNA"}
   {:code-point "\u0DA2", :character "ජ", :utf-8 "224 182 162", :description "SINHALA LETTER ALPAPRAANA JAYANNA"}
   {:code-point "\u0DA3", :character "ඣ", :utf-8 "224 182 163", :description "SINHALA LETTER MAHAAPRAANA JAYANNA"}
   {:code-point "\u0DA4", :character "ඤ", :utf-8 "224 182 164", :description "SINHALA LETTER TAALUJA NAASIKYAYA"}
   {:code-point "\u0DA5", :character "ඥ", :utf-8 "224 182 165", :description "SINHALA LETTER TAALUJA SANYOOGA NAAKSIKYAYA"}
   {:code-point "\u0DA6", :character "ඦ", :utf-8 "224 182 166", :description "SINHALA LETTER SANYAKA JAYANNA"}
   {:code-point "\u0DA7", :character "ට", :utf-8 "224 182 167", :description "SINHALA LETTER ALPAPRAANA TTAYANNA"}
   {:code-point "\u0DA8", :character "ඨ", :utf-8 "224 182 168", :description "SINHALA LETTER MAHAAPRAANA TTAYANNA"}
   {:code-point "\u0DA9", :character "ඩ", :utf-8 "224 182 169", :description "SINHALA LETTER ALPAPRAANA DDAYANNA"}
   {:code-point "\u0DAA", :character "ඪ", :utf-8 "224 182 170", :description "SINHALA LETTER MAHAAPRAANA DDAYANNA"}
   {:code-point "\u0DAB", :character "ණ", :utf-8 "224 182 171", :description "SINHALA LETTER MUURDHAJA NAYANNA"}
   {:code-point "\u0DAC", :character "ඬ", :utf-8 "224 182 172", :description "SINHALA LETTER SANYAKA DDAYANNA"}
   {:code-point "\u0DAD", :character "ත", :utf-8 "224 182 173", :description "SINHALA LETTER ALPAPRAANA TAYANNA"}
   {:code-point "\u0DAE", :character "ථ", :utf-8 "224 182 174", :description "SINHALA LETTER MAHAAPRAANA TAYANNA"}
   {:code-point "\u0DAF", :character "ද", :utf-8 "224 182 175", :description "SINHALA LETTER ALPAPRAANA DAYANNA"}
   {:code-point "\u0DB0", :character "ධ", :utf-8 "224 182 176", :description "SINHALA LETTER MAHAAPRAANA DAYANNA"}
   {:code-point "\u0DB1", :character "න", :utf-8 "224 182 177", :description "SINHALA LETTER DANTAJA NAYANNA"}
   {:code-point "\u0DB3", :character "ඳ", :utf-8 "224 182 179", :description "SINHALA LETTER SANYAKA DAYANNA"}
   {:code-point "\u0DB4", :character "ප", :utf-8 "224 182 180", :description "SINHALA LETTER ALPAPRAANA PAYANNA"}
   {:code-point "\u0DB5", :character "ඵ", :utf-8 "224 182 181", :description "SINHALA LETTER MAHAAPRAANA PAYANNA"}
   {:code-point "\u0DB6", :character "බ", :utf-8 "224 182 182", :description "SINHALA LETTER ALPAPRAANA BAYANNA"}
   {:code-point "\u0DB7", :character "භ", :utf-8 "224 182 183", :description "SINHALA LETTER MAHAAPRAANA BAYANNA"}
   {:code-point "\u0DB8", :character "ම", :utf-8 "224 182 184", :description "SINHALA LETTER MAYANNA"}
   {:code-point "\u0DB9", :character "ඹ", :utf-8 "224 182 185", :description "SINHALA LETTER AMBA BAYANNA"}
   {:code-point "\u0DBA", :character "ය", :utf-8 "224 182 186", :description "SINHALA LETTER YAYANNA"}
   {:code-point "\u0DBB", :character "ර", :utf-8 "224 182 187", :description "SINHALA LETTER RAYANNA"}
   {:code-point "\u0DBD", :character "ල", :utf-8 "224 182 189", :description "SINHALA LETTER DANTAJA LAYANNA"}
   {:code-point "\u0DC0", :character "ව", :utf-8 "224 183 128", :description "SINHALA LETTER VAYANNA"}
   {:code-point "\u0DC1", :character "ශ", :utf-8 "224 183 129", :description "SINHALA LETTER TAALUJA SAYANNA"}
   {:code-point "\u0DC2", :character "ෂ", :utf-8 "224 183 130", :description "SINHALA LETTER MUURDHAJA SAYANNA"}
   {:code-point "\u0DC3", :character "ස", :utf-8 "224 183 131", :description "SINHALA LETTER DANTAJA SAYANNA"}
   {:code-point "\u0DC4", :character "හ", :utf-8 "224 183 132", :description "SINHALA LETTER HAYANNA"}
   {:code-point "\u0DC5", :character "ළ", :utf-8 "224 183 133", :description "SINHALA LETTER MUURDHAJA LAYANNA"}
   {:code-point "\u0DC6", :character "ෆ", :utf-8 "224 183 134", :description "SINHALA LETTER FAYANNA"}
   {:code-point "\u0DCA", :character "්", :utf-8 "224 183 138", :description "SINHALA SIGN AL-LAKUNA"}
   {:code-point "\u0DCF", :character "ා", :utf-8 "224 183 143", :description "SINHALA VOWEL SIGN AELA-PILLA"}
   {:code-point "\u0DD0", :character "ැ", :utf-8 "224 183 144", :description "SINHALA VOWEL SIGN KETTI AEDA-PILLA"}
   {:code-point "\u0DD1", :character "ෑ", :utf-8 "224 183 145", :description "SINHALA VOWEL SIGN DIGA AEDA-PILLA"}
   {:code-point "\u0DD2", :character "ි", :utf-8 "224 183 146", :description "SINHALA VOWEL SIGN KETTI IS-PILLA"}
   {:code-point "\u0DD3", :character "ී", :utf-8 "224 183 147", :description "SINHALA VOWEL SIGN DIGA IS-PILLA"}
   {:code-point "\u0DD4", :character "ු", :utf-8 "224 183 148", :description "SINHALA VOWEL SIGN KETTI PAA-PILLA"}
   {:code-point "\u0DD6", :character "ූ", :utf-8 "224 183 150", :description "SINHALA VOWEL SIGN DIGA PAA-PILLA"}
   {:code-point "\u0DD8", :character "ෘ", :utf-8 "224 183 152", :description "SINHALA VOWEL SIGN GAETTA-PILLA"}
   {:code-point "\u0DD9", :character "ෙ", :utf-8 "224 183 153", :description "SINHALA VOWEL SIGN KOMBUVA"}
   {:code-point "\u0DDA", :character "ේ", :utf-8 "224 183 154", :description "SINHALA VOWEL SIGN DIGA KOMBUVA"}
   {:code-point "\u0DDB", :character "ෛ", :utf-8 "224 183 155", :description "SINHALA VOWEL SIGN KOMBU DEKA"}
   {:code-point "\u0DDC", :character "ො", :utf-8 "224 183 156", :description "SINHALA VOWEL SIGN KOMBUVA HAA AELA-PILLA"}
   {:code-point "\u0DDD", :character "ෝ", :utf-8 "224 183 157", :description "SINHALA VOWEL SIGN KOMBUVA HAA DIGA AELA-PILLA"}
   {:code-point "\u0DDE", :character "ෞ", :utf-8 "224 183 158", :description "SINHALA VOWEL SIGN KOMBUVA HAA GAYANUKITTA"}
   {:code-point "\u0DDF", :character "ෟ", :utf-8 "224 183 159", :description "SINHALA VOWEL SIGN GAYANUKITTA"}
   {:code-point "\u0DE6", :character "෦", :utf-8 "224 183 166", :description "SINHALA LITH DIGIT ZERO"}
   {:code-point "\u0DE7", :character "෧", :utf-8 "224 183 167", :description "SINHALA LITH DIGIT ONE"}
   {:code-point "\u0DE8", :character "෨", :utf-8 "224 183 168", :description "SINHALA LITH DIGIT TWO"}
   {:code-point "\u0DE9", :character "෩", :utf-8 "224 183 169", :description "SINHALA LITH DIGIT THREE"}
   {:code-point "\u0DEA", :character "෪", :utf-8 "224 183 170", :description "SINHALA LITH DIGIT FOUR"}
   {:code-point "\u0DEB", :character "෫", :utf-8 "224 183 171", :description "SINHALA LITH DIGIT FIVE"}
   {:code-point "\u0DEC", :character "෬", :utf-8 "224 183 172", :description "SINHALA LITH DIGIT SIX"}
   {:code-point "\u0DED", :character "෭", :utf-8 "224 183 173", :description "SINHALA LITH DIGIT SEVEN"}
   {:code-point "\u0DEE", :character "෮", :utf-8 "224 183 174", :description "SINHALA LITH DIGIT EIGHT"}
   {:code-point "\u0DEF", :character "෯", :utf-8 "224 183 175", :description "SINHALA LITH DIGIT NINE"}
   {:code-point "\u0DF2", :character "ෲ", :utf-8 "224 183 178", :description "SINHALA VOWEL SIGN DIGA GAETTA-PILLA"}
   {:code-point "\u0DF3", :character "ෳ", :utf-8 "224 183 179", :description "SINHALA VOWEL SIGN DIGA GAYANUKITTA"}
   {:code-point "\u0DF4", :character "෴", :utf-8 "224 183 180", :description "SINHALA PUNCTUATION KUNDDALIYA"}])

; Let's categorize
; \u0D85 to \u0D96 (including) are vowels.
; \u0D9A to \u0DC6 (including) are consonants
; \u0DCA makes pure consonant when combined with a consonant. We will categorize this as a vowel sign
; \u0DCF to \u0DDF (including), \u0DF2  are vowel signs. They combine with consonants.
; \u0D82 and \u0D83 are a category on their own, but used.
; Don't know the user of \u0DF3
; We don't need the ඏ and ඐ

; So we add categories to these. Manually this time. We remove unused or useless stuff.

(def sinhala-info
  [{:code-point "\u0D81", :character "ඁ", :category :unknown :utf-8 "224 182 129", :description "SINHALA SIGN CANDRABINDU"}
   {:code-point "\u0D82", :character "ං", :category :anusvaraya :utf-8 "224 182 130", :description "SINHALA SIGN ANUSVARAYA"}
   {:code-point "\u0D83", :character "ඃ", :category :visargaya :utf-8 "224 182 131", :description "SINHALA SIGN VISARGAYA"}
   {:code-point "\u0D85", :character "අ", :category :vowel :utf-8 "224 182 133", :description "SINHALA LETTER AYANNA"}
   {:code-point "\u0D86", :character "ආ", :category :vowel :utf-8 "224 182 134", :description "SINHALA LETTER AAYANNA"}
   {:code-point "\u0D87", :character "ඇ", :category :vowel :utf-8 "224 182 135", :description "SINHALA LETTER AEYANNA"}
   {:code-point "\u0D88", :character "ඈ", :category :vowel :utf-8 "224 182 136", :description "SINHALA LETTER AEEYANNA"}
   {:code-point "\u0D89", :character "ඉ", :category :vowel :utf-8 "224 182 137", :description "SINHALA LETTER IYANNA"}
   {:code-point "\u0D8A", :character "ඊ", :category :vowel :utf-8 "224 182 138", :description "SINHALA LETTER IIYANNA"}
   {:code-point "\u0D8B", :character "උ", :category :vowel :utf-8 "224 182 139", :description "SINHALA LETTER UYANNA"}
   {:code-point "\u0D8C", :character "ඌ", :category :vowel :utf-8 "224 182 140", :description "SINHALA LETTER UUYANNA"}
   {:code-point "\u0D8D", :character "ඍ", :category :vowel :utf-8 "224 182 141", :description "SINHALA LETTER IRUYANNA"}
   {:code-point "\u0D8E", :character "ඎ", :category :vowel :utf-8 "224 182 142", :description "SINHALA LETTER IRUUYANNA"}
   {:code-point "\u0D91", :character "එ", :category :vowel, :utf-8 "224 182 145", :description "SINHALA LETTER EYANNA"}
   {:code-point "\u0D92", :character "ඒ", :category :vowel, :utf-8 "224 182 146", :description "SINHALA LETTER EEYANNA"}
   {:code-point "\u0D93", :character "ඓ", :category :vowel, :utf-8 "224 182 147", :description "SINHALA LETTER AIYANNA"}
   {:code-point "\u0D94", :character "ඔ", :category :vowel, :utf-8 "224 182 148", :description "SINHALA LETTER OYANNA"}
   {:code-point "\u0D95", :character "ඕ", :category :vowel, :utf-8 "224 182 149", :description "SINHALA LETTER OOYANNA"}
   {:code-point "\u0D96", :character "ඖ", :category :vowel, :utf-8 "224 182 150", :description "SINHALA LETTER AUYANNA"}
   {:code-point "\u0D9A", :character "ක", :category :consonant, :utf-8 "224 182 154", :description "SINHALA LETTER ALPAPRAANA KAYANNA"}
   {:code-point "\u0D9B", :character "ඛ", :category :consonant, :utf-8 "224 182 155", :description "SINHALA LETTER MAHAAPRAANA KAYANNA"}
   {:code-point "\u0D9C", :character "ග", :category :consonant, :utf-8 "224 182 156", :description "SINHALA LETTER ALPAPRAANA GAYANNA"}
   {:code-point "\u0D9D", :character "ඝ", :category :consonant, :utf-8 "224 182 157", :description "SINHALA LETTER MAHAAPRAANA GAYANNA"}
   {:code-point "\u0D9E", :character "ඞ", :category :consonant, :utf-8 "224 182 158", :description "SINHALA LETTER KANTAJA NAASIKYAYA"}
   {:code-point "\u0D9F", :character "ඟ", :category :consonant, :utf-8 "224 182 159", :description "SINHALA LETTER SANYAKA GAYANNA"}
   {:code-point "\u0DA0", :character "ච", :category :consonant, :utf-8 "224 182 160", :description "SINHALA LETTER ALPAPRAANA CAYANNA"}
   {:code-point "\u0DA1", :character "ඡ", :category :consonant, :utf-8 "224 182 161", :description "SINHALA LETTER MAHAAPRAANA CAYANNA"}
   {:code-point "\u0DA2", :character "ජ", :category :consonant, :utf-8 "224 182 162", :description "SINHALA LETTER ALPAPRAANA JAYANNA"}
   {:code-point "\u0DA3", :character "ඣ", :category :consonant, :utf-8 "224 182 163", :description "SINHALA LETTER MAHAAPRAANA JAYANNA"}
   {:code-point "\u0DA4", :character "ඤ", :category :consonant, :utf-8 "224 182 164", :description "SINHALA LETTER TAALUJA NAASIKYAYA"}
   {:code-point "\u0DA5", :character "ඥ", :category :consonant, :utf-8 "224 182 165", :description "SINHALA LETTER TAALUJA SANYOOGA NAAKSIKYAYA"}
   {:code-point "\u0DA6", :character "ඦ", :category :consonant, :utf-8 "224 182 166", :description "SINHALA LETTER SANYAKA JAYANNA"}
   {:code-point "\u0DA7", :character "ට", :category :consonant, :utf-8 "224 182 167", :description "SINHALA LETTER ALPAPRAANA TTAYANNA"}
   {:code-point "\u0DA8", :character "ඨ", :category :consonant, :utf-8 "224 182 168", :description "SINHALA LETTER MAHAAPRAANA TTAYANNA"}
   {:code-point "\u0DA9", :character "ඩ", :category :consonant, :utf-8 "224 182 169", :description "SINHALA LETTER ALPAPRAANA DDAYANNA"}
   {:code-point "\u0DAA", :character "ඪ", :category :consonant, :utf-8 "224 182 170", :description "SINHALA LETTER MAHAAPRAANA DDAYANNA"}
   {:code-point "\u0DAB", :character "ණ", :category :consonant, :utf-8 "224 182 171", :description "SINHALA LETTER MUURDHAJA NAYANNA"}
   {:code-point "\u0DAC", :character "ඬ", :category :consonant, :utf-8 "224 182 172", :description "SINHALA LETTER SANYAKA DDAYANNA"}
   {:code-point "\u0DAD", :character "ත", :category :consonant, :utf-8 "224 182 173", :description "SINHALA LETTER ALPAPRAANA TAYANNA"}
   {:code-point "\u0DAE", :character "ථ", :category :consonant, :utf-8 "224 182 174", :description "SINHALA LETTER MAHAAPRAANA TAYANNA"}
   {:code-point "\u0DAF", :character "ද", :category :consonant, :utf-8 "224 182 175", :description "SINHALA LETTER ALPAPRAANA DAYANNA"}
   {:code-point "\u0DB0", :character "ධ", :category :consonant, :utf-8 "224 182 176", :description "SINHALA LETTER MAHAAPRAANA DAYANNA"}
   {:code-point "\u0DB1", :character "න", :category :consonant, :utf-8 "224 182 177", :description "SINHALA LETTER DANTAJA NAYANNA"}
   {:code-point "\u0DB3", :character "ඳ", :category :consonant, :utf-8 "224 182 179", :description "SINHALA LETTER SANYAKA DAYANNA"}
   {:code-point "\u0DB4", :character "ප", :category :consonant, :utf-8 "224 182 180", :description "SINHALA LETTER ALPAPRAANA PAYANNA"}
   {:code-point "\u0DB5", :character "ඵ", :category :consonant, :utf-8 "224 182 181", :description "SINHALA LETTER MAHAAPRAANA PAYANNA"}
   {:code-point "\u0DB6", :character "බ", :category :consonant, :utf-8 "224 182 182", :description "SINHALA LETTER ALPAPRAANA BAYANNA"}
   {:code-point "\u0DB7", :character "භ", :category :consonant, :utf-8 "224 182 183", :description "SINHALA LETTER MAHAAPRAANA BAYANNA"}
   {:code-point "\u0DB8", :character "ම", :category :consonant, :utf-8 "224 182 184", :description "SINHALA LETTER MAYANNA"}
   {:code-point "\u0DB9", :character "ඹ", :category :consonant, :utf-8 "224 182 185", :description "SINHALA LETTER AMBA BAYANNA"}
   {:code-point "\u0DBA", :character "ය", :category :consonant, :utf-8 "224 182 186", :description "SINHALA LETTER YAYANNA"}
   {:code-point "\u0DBB", :character "ර", :category :consonant, :utf-8 "224 182 187", :description "SINHALA LETTER RAYANNA"}
   {:code-point "\u0DBD", :character "ල", :category :consonant, :utf-8 "224 182 189", :description "SINHALA LETTER DANTAJA LAYANNA"}
   {:code-point "\u0DC0", :character "ව", :category :consonant, :utf-8 "224 183 128", :description "SINHALA LETTER VAYANNA"}
   {:code-point "\u0DC1", :character "ශ", :category :consonant, :utf-8 "224 183 129", :description "SINHALA LETTER TAALUJA SAYANNA"}
   {:code-point "\u0DC2", :character "ෂ", :category :consonant, :utf-8 "224 183 130", :description "SINHALA LETTER MUURDHAJA SAYANNA"}
   {:code-point "\u0DC3", :character "ස", :category :consonant, :utf-8 "224 183 131", :description "SINHALA LETTER DANTAJA SAYANNA"}
   {:code-point "\u0DC4", :character "හ", :category :consonant, :utf-8 "224 183 132", :description "SINHALA LETTER HAYANNA"}
   {:code-point "\u0DC5", :character "ළ", :category :consonant, :utf-8 "224 183 133", :description "SINHALA LETTER MUURDHAJA LAYANNA"}
   {:code-point "\u0DC6", :character "ෆ", :category :consonant, :utf-8 "224 183 134", :description "SINHALA LETTER FAYANNA"}
   {:code-point "\u0DCA", :character "්", :category :vowel-sign, :utf-8 "224 183 138", :description "SINHALA SIGN AL-LAKUNA"}
   {:code-point "\u0DCF", :character "ා", :category :vowel-sign, :utf-8 "224 183 143", :description "SINHALA VOWEL SIGN AELA-PILLA"}
   {:code-point "\u0DD0", :character "ැ", :category :vowel-sign, :utf-8 "224 183 144", :description "SINHALA VOWEL SIGN KETTI AEDA-PILLA"}
   {:code-point "\u0DD1", :character "ෑ", :category :vowel-sign, :utf-8 "224 183 145", :description "SINHALA VOWEL SIGN DIGA AEDA-PILLA"}
   {:code-point "\u0DD2", :character "ි", :category :vowel-sign, :utf-8 "224 183 146", :description "SINHALA VOWEL SIGN KETTI IS-PILLA"}
   {:code-point "\u0DD3", :character "ී", :category :vowel-sign, :utf-8 "224 183 147", :description "SINHALA VOWEL SIGN DIGA IS-PILLA"}
   {:code-point "\u0DD4", :character "ු", :category :vowel-sign, :utf-8 "224 183 148", :description "SINHALA VOWEL SIGN KETTI PAA-PILLA"}
   {:code-point "\u0DD6", :character "ූ", :category :vowel-sign, :utf-8 "224 183 150", :description "SINHALA VOWEL SIGN DIGA PAA-PILLA"}
   {:code-point "\u0DD8", :character "ෘ", :category :vowel-sign, :utf-8 "224 183 152", :description "SINHALA VOWEL SIGN GAETTA-PILLA"}
   {:code-point "\u0DD9", :character "ෙ", :category :vowel-sign, :utf-8 "224 183 153", :description "SINHALA VOWEL SIGN KOMBUVA"}
   {:code-point "\u0DDA", :character "ේ", :category :vowel-sign, :utf-8 "224 183 154", :description "SINHALA VOWEL SIGN DIGA KOMBUVA"}
   {:code-point "\u0DDB", :character "ෛ", :category :vowel-sign, :utf-8 "224 183 155", :description "SINHALA VOWEL SIGN KOMBU DEKA"}
   {:code-point "\u0DDC", :character "ො", :category :vowel-sign, :utf-8 "224 183 156", :description "SINHALA VOWEL SIGN KOMBUVA HAA AELA-PILLA"}
   {:code-point "\u0DDD", :character "ෝ", :category :vowel-sign, :utf-8 "224 183 157", :description "SINHALA VOWEL SIGN KOMBUVA HAA DIGA AELA-PILLA"}
   {:code-point "\u0DDE", :character "ෞ", :category :vowel-sign, :utf-8 "224 183 158", :description "SINHALA VOWEL SIGN KOMBUVA HAA GAYANUKITTA"}
   {:code-point "\u0DDF", :character "ෟ", :category :vowel-sign, :utf-8 "224 183 159", :description "SINHALA VOWEL SIGN GAYANUKITTA"}
   {:code-point "\u0DF2", :character "ෲ", :category :vowel-sign, :utf-8 "224 183 178", :description "SINHALA VOWEL SIGN DIGA GAETTA-PILLA"}
   {:code-point "\u0DF4", :character "෴", :category :kundaaliya, :utf-8 "224 183 180", :description "SINHALA PUNCTUATION KUNDDALIYA"}])


; Suppose our input string is "wyaakaraNa"
; We'd have to have multiple replacement steps based on their specificity.
; For example, "wyaa" should map to "ව්‍යා" in one pass. This rule is more specific than say "aa" -> ආ.
; So we can imagine the replace passes and their results like this:
; 1. "wyaakaraNa" - input
; 2. "wyaakaraNa" - replace "wyaa"
; 2. "ව්‍යාkaraNa" - replace "ka", "ra", "Na"
; 3. "ව්‍යාකරණ" - output

; So why don't we try to write the code for this now?

(require '[clojure.string :as str])

; Most specific rules come first.
(def transliteration-rules
  [{"wyaa" "ව්‍යා"}
   {"ka" "ක"
    "ra" "ර"
    "Na" "ණ"}])

; So we can do something like this.
; It's a reduce in reduce.
; Basically, keep replacing with most specific rules first, then the rest until we run out of rules.
(defn transliterate [input]
  (reduce (fn [in rule-level]
            (reduce (fn [in rule] (str/replace in (re-pattern (first rule)) (second rule)))
                    in rule-level))
          input transliteration-rules))

(transliterate "wyaakaraNa")
;; => "ව්‍යාකරණ"


; Get instaparse in
(require '[instaparse.core :as insta])

; Let's try something.
(def p (insta/parser "vowel='a'|'aa'|'sru'"))

(comment
  (p "aa")
  ;; => [:vowel "aa"]
  (p "sru")
  ;; => [:vowel "sru"]
  (p "r")
  ;; => {:index 0,
  ;;     :reason
  ;;     [{:tag :string, :expecting "sru", :full true}
  ;;      {:tag :string, :expecting "aa", :full true}
  ;;      {:tag :string, :expecting "a", :full true}],
  ;;     :line 1,
  ;;     :column 1,
  ;;     :text "r"}
  )

; Let's define transliteration mappings for vowels
(def transliteration-vowel-mapping
  {"a" "අ"
   "aa" "ආ"
   "A" "ඇ"
   "AA" "ඈ"
   "i" "ඉ"
   "ii" "ඊ"
   "ee" "ඊ"
   "u" "උ"
   "U" "ඌ"
   "sru" "ඍ"
   "srU" "ඎ"
   "e" "එ"
   "E" "ඒ"
   "I" "ඓ"
   "ei" "ඓ"
   "o" "ඔ"
   "O" "ඕ"
   "ou" "ඖ"
   "au" "ඖ"})

; Note that some vowels have two or more english letters map to them (i.e ඓ). But there's no ambiguity about what a seq of English letters match to.
; Let's convert this to a grammar that instaparse understands.
; We only need the keys.
(def vowel-rule
  (->> transliteration-vowel-mapping
       keys
       (map #(str "'" % "'"))
       (str/join "|")
       (str "vowel=")))
;; => "vowel='ee'|'aa'|'sru'|'ii'|'e'|'ei'|'E'|'srU'|'a'|'U'|'O'|'i'|'AA'|'u'|'A'|'I'|'ou'|'o'|'au'"

; And let's make a parser out of it. We need to define first rule to be the sum. i.e Full parser grammar like this:
; "S=vowel*
;  vowel='ee'|'aa'|'sru'|'ii'|'e'|'ei'|'E'|'srU'|'a'|'U'|'O'|'i'|'AA'|'u'|'A'|'I'|'ou'|'o'|'au'"

(def parser-grammar
  (str "S=vowel*" "\n" vowel-rule))

(def vowel-trans-parser
  (insta/parser parser-grammar))

; Let's use it in couple of cases

(comment
  (vowel-trans-parser "srU")
  ;; => [:S [:vowel "srU"]]
  (vowel-trans-parser "aa")
  ;; => [:S [:vowel "aa"]] 
  (vowel-trans-parser "Ie")
  ;; => [:S [:vowel "I"] [:vowel "e"]]
  (vowel-trans-parser "OO")
  ;; => [:S [:vowel "O"] [:vowel "O"]]
  (vowel-trans-parser "ee")
  ;; => [:S [:vowel "ee"]]
  ;; => [:S [:vowel "e"] [:vowel "e"]]
  )

((insta/parser "S=vowel*;vowel='ee'|'e'") "ee")
;; => [:S [:vowel "e"] [:vowel "e"]]

((insta/parser "S=vowel*;vowel='e'|'ee'") "ee")
;; => [:S [:vowel "ee"]]

(def vowel-rule
  (->> transliteration-vowel-mapping
       (keys)
       (sort-by #(.length %))
       (map #(str "'" % "'"))
       (str/join "|")
       (str "vowel=")))

(def transliteration-pureconsonant-mapping
  {"k" "ක්"
   "K" "ඛ්"
   "g" "ග්"
   "G" "ඝ්"
   ;"" "ඞ"
   "xg" "ඟ්"
   "ch" "ච්"
   "chh" "ඡ්"
   "Ch" "ඡ්"
   "j" "ජ්"
   "J" "ඣ"
   "xkdh" "ඤ්"
   "xgdh" "ඥ්"
   "xj" "ඦ"
   "t" "ට්"
   "T" "ඨ්"
   "d" "ඩ්"
   "D" "ඪ්"
   "N" "ණ්"
   "xd" "ඬ්"
   "th" "ත්"
   "Th" "ථ්"
   "thh" "ථ්"
   "dh" "ද්"
   "Dh" "ධ්"
   "n" "න්"
   "xdh" "ඳ්"
   "p" "ප්"
   "P" "ඵ්"
   "b" "බ්"
   "B" "භ්"
   "m" "ම්"
   "xb" "ඹ්"
   "xmb" "ඹ්"
   "y" "ය්"
   "r" "ර්"
   "l" "ල්"
   "w" "ව්"
   "v" "ව්"
   "sh" "ශ්"
   "Sh" "ෂ්"
   "s" "ස්"
   "h" "හ්"
   "L" "ළ්"
   "f" "ෆ්"})

(def pureconsonant-rule
  (->> transliteration-pureconsonant-mapping
       (keys)
       (sort-by #(.length %))
       (map #(str "'" % "'"))
       (str/join "|")
       (str "pureconsonant=")))

(def parser-grammar
  (str "S=word (whitespace word)*" ";\n"
       "word=(vowel|pureconsonant|consonant|whitespace)+"
       vowel-rule ";\n"
       pureconsonant-rule ";\n"
       "consonant=pureconsonant,vowel" ";\n"
       "whitespace=#'(\\s|\\n|\\t)+'"))

(def parser
  (insta/parser parser-grammar))

(comment
  (parser "a")
  ;; => [:S [:word [:vowel "a"]]]

  (parser "ala")
  ;; => [:S [:word [:vowel "a"] [:consonant [:pureconsonant "l"] [:vowel "a"]]]]

  (parser "adha ala")
  ;; => [:S
  ;;     [:word [:vowel "a"] [:consonant [:pureconsonant "dh"] [:vowel "a"]]]
  ;;     [:whitespace " "]
  ;;     [:word [:vowel "a"] [:consonant [:pureconsonant "l"] [:vowel "a"]]]]

  (parser "adi")
  ;; => [:S [:word [:vowel "a"] [:consonant [:pureconsonant "d"] [:vowel "i"]]]]

  (parser "waasanaa")
  ;; => [:S
  ;;     [:word
  ;;      [:consonant [:pureconsonant "w"] [:vowel "aa"]]
  ;;      [:consonant [:pureconsonant "s"] [:vowel "a"]]
  ;;      [:consonant [:pureconsonant "n"] [:vowel "aa"]]]]

  (parser "thAna thAna rawumata ixdhagena")
  ;; => [:S
  ;;     [:word [:consonant [:pureconsonant "th"] [:vowel "A"]] [:consonant [:pureconsonant "n"] [:vowel "a"]]]
  ;;     [:whitespace " "]
  ;;     [:word [:consonant [:pureconsonant "th"] [:vowel "A"]] [:consonant [:pureconsonant "n"] [:vowel "a"]]]
  ;;     [:whitespace " "]
  ;;     [:word
  ;;      [:consonant [:pureconsonant "r"] [:vowel "a"]]
  ;;      [:consonant [:pureconsonant "w"] [:vowel "u"]]
  ;;      [:consonant [:pureconsonant "m"] [:vowel "a"]]
  ;;      [:consonant [:pureconsonant "t"] [:vowel "a"]]]
  ;;     [:whitespace " "]
  ;;     [:word
  ;;      [:vowel "i"]
  ;;      [:consonant [:pureconsonant "xdh"] [:vowel "a"]]
  ;;      [:consonant [:pureconsonant "g"] [:vowel "e"]]
  ;;      [:consonant [:pureconsonant "n"] [:vowel "a"]]]]

  )

(def transliteration-vowel-sign-mapping
  {"a" ""
   "aa" "ා"
   "A" "ැ"
   "AA" "ෑ"
   "i" "ි"
   "ii" "ී"
   "ee" "ු"
   "u" "ු"
   "U" "ූ"
   "sru" "ෘ"
   "srU" "ෲ"
   "e" "ෙ"
   "E" "ේ"
   "I" "ෛ"
   "ei" "ෛ"
   "o" "ො"
   "O" "ෝ"
   "ou" "ෞ"
   "au" "ෞ"})

(def vowel-sign-vowel-mapping
  {"අ" ""
   "ආ" "ා"
   "ඇ" "ැ"
   "ඈ" "ෑ"
   "ඉ" "ි"
   "ඊ" "ී"
   "උ" "ු"
   "ඌ" "ූ"
   "ඍ" "ෘ"
   "ඎ" "ෲ"
   "එ" "ෙ"
   "ඒ" "ේ"
   "ඓ" "ෛ" 
   "ඔ" "ො"
   "ඕ" "ෝ"
   "ඖ" "ෞ"})

(defn combine [c v]
  (let [c (subs c 0 1)]
    (if (= v "අ")
      c (str c (vowel-sign-vowel-mapping v)))))

(combine "ස්" "අ")
;; => "ස"

(combine "ස්" "ඖ")
;; => "සෞ"


(defn transliterate [text]
  (insta/transform
   {:pureconsonant
    #(if (string? %) (transliteration-pureconsonant-mapping %) %)
    :vowel
    #(if (string? %) (transliteration-vowel-mapping %) %)
    :consonant
    (fn [pureconsonant vowel]
      (if (and (string? pureconsonant) (string? vowel))
        (combine pureconsonant vowel)
        "test"))
    :word str
    :whitespace identity
    :S str}
   (parser text)))

(def song (str "obe nil nuwan thalaawE sAlE kaxdhuLu midhilaa\n"
               "mata E mAdhin penennE apE mathaka mAwilaa\n"
               "oya dhAAtha maage dhAAthE\n"
               "sathapaa gewU athiithE\n"
               "dhedhenaa nowEdha dhannE\n"
               "sitha dhAn thamayi sithannE\n"))

(transliterate song)
;; => "ඔබෙ නිල් නුවන් තලාවේ සැලේ කඳුළු මිදිලා\nමට ඒ මැදින් පෙනෙන්නේ අපේ මතක මැවිලා\nඔය දෑත මාගෙ දෑතේ\nසතපා ගෙවූ අතීතේ\nදෙදෙනා නොවේද දන්නේ\nසිත දැන් තමයි සිතන්නේ\n"


(comment
  (transliterate "a")
  (transliterate "ala")
  (transliterate "adha ala")
  (transliterate "adi")
  (transliterate "waasanaa")
  (transliterate "thAna thAna rawumata ixdhagena")
  (transliterate "oba laxga inna laxga inna mage waasanaa")
  (time (transliterate song))
  (parser song)
  (time 
   (doall (map transliterate (str/split song #"(\s|\n)+"))))
  ,)


