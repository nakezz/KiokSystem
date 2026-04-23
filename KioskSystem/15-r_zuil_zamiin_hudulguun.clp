(deftemplate intersection
   (slot type (allowed-symbols regulated unregulated-equal main-road minor-road none))
   (slot traffic-jam (allowed-symbols yes no)))

(deftemplate driver-action
   (slot direction (allowed-symbols straight turn-right turn-left u-turn)))

(deftemplate environment
   (slot pedestrian (allowed-symbols yes no))
   (slot car-on-right (allowed-symbols yes no))
   (slot car-opposite-straight-right (allowed-symbols yes no)))

(defrule rule-15-2-traffic-jam
   (intersection (traffic-jam yes))
   =>
   (printout t "[15.2] ХОРИГЛОНО: Уулзварт түгжрэл үүссэн тул орохыг хориглоно!" crlf)
   (halt)) 

(defrule rule-15-1-pedestrian
   (driver-action (direction turn-right|turn-left))
   (environment (pedestrian yes))
   =>
   (printout t "[15.1] АНХААР: Баруун/зүүн эргэхдээ явган зорчигчид зам тавьж өгнө!" crlf))

(defrule rule-15-4-regulated-left
   (intersection (type regulated))
   (driver-action (direction turn-left|u-turn))
   (environment (car-opposite-straight-right yes))
   =>
   (printout t "[15.4] АНХААР: Өөдөөс чигээрээ/баруун эргэх тээврийн хэрэгсэлд зам тавьж өгнө!" crlf))

(defrule rule-15-8-equal-road
   (intersection (type unregulated-equal))
   (environment (car-on-right yes))
   =>
   (printout t "[15.8] АНХААР: Баруун гар талаас ирсэн тээврийн хэрэгсэлд зам тавьж өгнө!" crlf))

(defrule rule-15-9-minor-road
   (intersection (type minor-road))
   =>
   (printout t "[15.9] АНХААР: Гол замаас ирж яваа тээврийн хэрэгсэлд зам тавьж өгнө!" crlf))
(defrule rule-15-10-unregulated-left
   (intersection (type unregulated-equal|main-road|minor-road))
   (driver-action (direction turn-left|u-turn))
   (environment (car-opposite-straight-right yes))
   =>
   (printout t "[15.10] АНХААР: Адил замаар өөдөөс чигээрээ/баруун эргэх машинд зам тавьж өгнө!" crlf))

(deffacts test-scenario
   (intersection (type unregulated-equal) (traffic-jam no))
   (driver-action (direction turn-left))
   (environment (pedestrian yes) (car-on-right yes) (car-opposite-straight-right yes)))