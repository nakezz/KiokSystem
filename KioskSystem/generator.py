import random

courses = [
    ("F.ITM201", "C++ Programming", "F.IT01"),
    ("F.ITM202", "Algorithms", "F.IT02"),
    ("F.ITM203", "Data Structures", "F.IT03"),
    ("F.ITM204", "Database Systems", "F.IT04"),
    ("F.ITM205", "Computer Networks", "F.IT05"),
    ("F.ITM206", "Web Development", "F.IT06"),
    ("F.ITM207", "Artificial Intelligence", "F.IT07"),
    ("F.ITM208", "Machine Learning", "F.IT08"),
    ("F.ITM209", "Operating Systems", "F.IT09"),
    ("F.ITM210", "Software Engineering", "F.IT10")
]

teachers = [
    ("F.IT01", "Dr. Bat"),
    ("F.IT02", "Dr. Saraa"),
    ("F.IT03", "Dr. Bold"),
    ("F.IT04", "Dr. Oyuka"),
    ("F.IT05", "Dr. Dorj"),
    ("F.IT06", "Dr. Narantuya"),
    ("F.IT07", "Dr. Ganbold"),
    ("F.IT08", "Dr. Ts. Sugir"),
    ("F.IT09", "Dr. Altangerel"),
    ("F.IT10", "Dr. Khishigjargal")
]

names = ["Temuulen", "Anand", "Nomin", "Khulan", "Bilguun", "Anu", "Tuguldur", "Sarnai", "Maral", "Javkhlan",
         "Bat", "Bold", "Tulga", "Gantulga", "Nandin", "Sodnom", "Bayar", "Dulguun", "Tenuun", "Huslen"]

with open("initial_data.clp", "w", encoding="utf-8") as f:
    f.write('(deffacts initial-data\n')
    
    for t in teachers:
        f.write(f'   (teacher (id "{t[0]}") (name "{t[1]}"))\n')
        
    for c in courses:
        # capacity is low (5) to simulate congestion
        f.write(f'   (course (id "{c[0]}") (name "{c[1]}") (teacher_id "{c[2]}") (capacity 5))\n')
        
    for i in range(1, 26):
        name = random.choice(names)
        sid = f"B241960{i:03d}"
        f.write(f'   (student (id "{sid}") (name "{name}") (type "freshman"))\n')
        # Freshmen are enrolled in 1-2 active courses
        for _ in range(2):
            cid = random.choice(courses)[0]
            f.write(f'   (enrollment (student_id "{sid}") (course_id "{cid}") (status "Active"))\n')
            
    for i in range(1, 26):
        name = random.choice(names)
        sid = f"B211960{i:03d}"
        f.write(f'   (student (id "{sid}") (name "{name}") (type "senior"))\n')
        # Seniors have passed 2 courses
        for _ in range(2):
            cid = random.choice(courses)[0]
            f.write(f'   (enrollment (student_id "{sid}") (course_id "{cid}") (status "Passed"))\n')
            
    f.write(')\n')
