SELECT  childParent.id as child, childParent.father_id as parent, parentgp.father_id as gp FROM individual as childParent
INNER JOIN individual as parentgp
ON childParent.father_id=parentgp.id
where childParent.generation>1
UNION
SELECT childParent.id as child, childParent.father_id as parent, parentgp.mother_id as gp FROM individual as childParent
INNER JOIN individual as parentgp
ON childParent.mother_id=parentgp.id
where childParent.generation>1
UNION
SELECT childParent.id as child, childParent.mother_id as parent, parentgp.father_id as gp FROM individual as childParent
INNER JOIN individual as parentgp
ON childParent.father_id=parentgp.id
where childParent.generation>1
UNION
SELECT childParent.id as child, childParent.mother_id as parent, parentgp.mother_id as gp FROM individual as childParent
INNER JOIN individual as parentgp
ON childParent.mother_id=parentgp.id
where childParent.generation>1




--SELECT  childParent.id as child, childParent.father_id as parent, parentgp.father_id as gp FROM individual as childParent INNER JOIN individual as parentgp ON childParent.father_id=parentgp.id where childParent.generation>1 UNION SELECT childParent.id as child, childParent.father_id as parent, parentgp.mother_id as gp FROM individual as childParent INNER JOIN individual as parentgp ON childParent.mother_id=parentgp.id where childParent.generation>1 UNION SELECT childParent.id as child, childParent.mother_id as parent, parentgp.father_id as gp FROM individual as childParent INNER JOIN individual as parentgp ON childParent.father_id=parentgp.id where childParent.generation>1 UNION SELECT childParent.id as child, childParent.mother_id as parent, parentgp.mother_id as gp FROM individual as childParent INNER JOIN individual as parentgp ON childParent.mother_id=parentgp.id where childParent.generation>1