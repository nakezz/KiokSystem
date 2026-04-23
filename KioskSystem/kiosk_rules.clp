;;; =========================================================
;;; MUST Kiosk Registration System - CLIPS Logic
;;; =========================================================

;;; ---------------------------------------------------------
;;; TEMPLATES (Data Structures)
;;; ---------------------------------------------------------

(deftemplate student
   (slot id (type STRING))
   (slot name (type STRING))
   (slot type (type STRING)) ; "freshman" or "senior"
)

(deftemplate teacher
   (slot id (type STRING))
   (slot name (type STRING))
)

(deftemplate course
   (slot id (type STRING))
   (slot name (type STRING))
   (slot teacher_id (type STRING))
   (slot capacity (type INTEGER) (default 5)) ; Small capacity to test full class
)

(deftemplate enrollment
   (slot student_id (type STRING))
   (slot course_id (type STRING))
   (slot status (type STRING) (default "Active")) ; "Active", "R", "E", "Passed"
)

(deftemplate extra-slot
   (slot course_id (type STRING))
   (slot count (type INTEGER) (default 0))
)

;;; Actions triggered by Java
(deftemplate Action-Enroll
   (slot student_id (type STRING))
   (slot course_id (type STRING))
)

(deftemplate Action-Drop
   (slot student_id (type STRING))
   (slot course_id (type STRING))
)

(deftemplate Action-Status
   (slot student_id (type STRING))
   (slot course_id (type STRING))
   (slot new_status (type STRING))
)

;;; ---------------------------------------------------------
;;; FUNCTIONS
;;; ---------------------------------------------------------

;; Function to count active enrollments for a course
(deffunction count-enrollments (?c-id)
   (bind ?count 0)
   (do-for-all-facts ((?e enrollment)) (eq ?e:course_id ?c-id)
      (if (or (eq ?e:status "Active") (eq ?e:status "R") (eq ?e:status "E")) then
         (bind ?count (+ ?count 1))
      )
   )
   (return ?count)
)

;; Function to get total capacity including extra slots
(deffunction get-total-capacity (?c-id ?base-capacity)
   (bind ?total ?base-capacity)
   (do-for-all-facts ((?x extra-slot)) (eq ?x:course_id ?c-id)
      (bind ?total (+ ?total (* ?x:count 5))) ; Each extra slot adds 5 capacity
   )
   (return ?total)
)

;; Function to output to Java
(deffunction write-out (?text)
   (open "out.dat" outfile "a")
   (printout outfile ?text crlf)
   (close outfile)
)

;;; ---------------------------------------------------------
;;; RULES
;;; ---------------------------------------------------------

;;; 1. ENROLLMENT LOGIC

(defrule process-enrollment
   ?action <- (Action-Enroll (student_id ?s-id) (course_id ?c-id))
   (course (id ?c-id) (capacity ?base-cap) (name ?c-name))
   (student (id ?s-id) (name ?s-name))
   =>
   (retract ?action)
   
   ;; Check if already enrolled
   (bind ?already (find-all-facts ((?e enrollment)) (and (eq ?e:student_id ?s-id) (eq ?e:course_id ?c-id))))
   
   (if (> (length$ ?already) 0) then
      (write-out (str-cat "FAIL: Оюутан " ?s-name " энэ хичээлийг аль хэдийн судалж байна."))
   else
      ;; Check capacity
      (bind ?current (count-enrollments ?c-id))
      (bind ?max (get-total-capacity ?c-id ?base-cap))
      
      (if (>= ?current ?max) then
         ;; CLASS IS FULL -> AUTO CREATE EXTRA SLOT
         (write-out (str-cat "INFO: '" ?c-name "' хичээл дүүрсэн тул НЭМЭЛТ ЦАГ (Extra Slot) үүсгэлээ!"))
         
         ;; Find and update extra-slot fact or create one
         (bind ?slots (find-all-facts ((?x extra-slot)) (eq ?x:course_id ?c-id)))
         (if (> (length$ ?slots) 0) then
            (bind ?slot-fact (nth$ 1 ?slots))
            (modify ?slot-fact (count (+ (fact-slot-value ?slot-fact count) 1)))
         else
            (assert (extra-slot (course_id ?c-id) (count 1)))
         )
      )
      
      ;; Enroll student
      (assert (enrollment (student_id ?s-id) (course_id ?c-id) (status "Active")))
      (write-out (str-cat "SUCCESS: Оюутан " ?s-name " '" ?c-name "' хичээлд амжилттай бүртгүүллээ."))
   )
)


;;; 2. DROP LOGIC

(defrule process-drop
   ?action <- (Action-Drop (student_id ?s-id) (course_id ?c-id))
   ?enroll <- (enrollment (student_id ?s-id) (course_id ?c-id))
   =>
   (retract ?action)
   (retract ?enroll)
   (write-out "SUCCESS: Хичээлээс амжилттай гарлаа.")
)

(defrule process-drop-not-found
   ?action <- (Action-Drop (student_id ?s-id) (course_id ?c-id))
   (not (enrollment (student_id ?s-id) (course_id ?c-id)))
   =>
   (retract ?action)
   (write-out "FAIL: Та энэ хичээлд бүртгүүлээгүй байна.")
)


;;; 3. R / E STATUS LOGIC

(defrule process-status
   ?action <- (Action-Status (student_id ?s-id) (course_id ?c-id) (new_status ?status))
   ?enroll <- (enrollment (student_id ?s-id) (course_id ?c-id) (status ?curr))
   =>
   (retract ?action)
   (if (eq ?curr "Passed") then
      (write-out "FAIL: Энэ хичээлийг аль хэдийн судалж тэнцсэн байна.")
   else
      (modify ?enroll (status ?status))
      (write-out (str-cat "SUCCESS: Хичээлийн төлөвийг '" ?status "' болгож өөрчиллөө."))
   )
)

(defrule process-status-not-found
   ?action <- (Action-Status (student_id ?s-id) (course_id ?c-id) (new_status ?status))
   (not (enrollment (student_id ?s-id) (course_id ?c-id)))
   =>
   (retract ?action)
   (write-out "FAIL: Та энэ хичээлийг судлаагүй тул хүсэлт гаргах боломжгүй.")
)
