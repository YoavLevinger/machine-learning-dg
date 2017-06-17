SELECT  childParent.id as child, childParent.father_id as parent, parentgp.father_id as gp, brother.id as uncle FROM individual as childParent

INNER JOIN individual as parentgp
ON childParent.father_id=parentgp.id

INNER JOIN individual as brother
ON parentgp.father_id = brother.father_id

where childparent.generation>1 AND childparent.father_id <> brother.id

union

SELECT  childParent.id as child, childParent.mother_id as parent, parentgp.father_id as gp, brother.id as uncle FROM individual as childParent

INNER JOIN individual as parentgp
ON childParent.mother_id=parentgp.id

INNER JOIN individual as brother
ON parentgp.father_id = brother.father_id

where childparent.generation>1 AND childparent.mother_id <> brother.id

union

SELECT  childParent.id as child, childParent.mother_id as parent, parentgp.mother_id as gp, brother.id as uncle FROM individual as childParent

INNER JOIN individual as parentgp
ON childParent.mother_id=parentgp.id

INNER JOIN individual as brother
ON parentgp.mother_id = brother.mother_id

where childparent.generation>1 AND childparent.mother_id <> brother.id

union

SELECT  childParent.id as child, childParent.father_id as parent, parentgp.mother_id as gp, brother.id as uncle FROM individual as childParent

INNER JOIN individual as parentgp
ON childParent.father_id=parentgp.id

INNER JOIN individual as brother
ON parentgp.mother_id = brother.mother_id

where childparent.generation>1 AND childparent.father_id <> brother.id


--SELECT  childParent.id as child, childParent.father_id as parent, parentgp.father_id as gp, brother.id as uncle FROM individual as childParent INNER JOIN individual as parentgp ON childParent.father_id=parentgp.id INNER JOIN individual as brother ON parentgp.father_id = brother.father_id where childparent.generation>1 AND childparent.father_id <> brother.id union SELECT  childParent.id as child, childParent.mother_id as parent, parentgp.father_id as gp, brother.id as uncle FROM individual as childParent INNER JOIN individual as parentgp ON childParent.mother_id=parentgp.id INNER JOIN individual as brother ON parentgp.father_id = brother.father_id where childparent.generation>1 AND childparent.mother_id <> brother.id union SELECT  childParent.id as child, childParent.mother_id as parent, parentgp.mother_id as gp, brother.id as uncle FROM individual as childParent INNER JOIN individual as parentgp ON childParent.mother_id=parentgp.id INNER JOIN individual as brother ON parentgp.mother_id = brother.mother_id where childparent.generation>1 AND childparent.mother_id <> brother.id union SELECT  childParent.id as child, childParent.father_id as parent, parentgp.mother_id as gp, brother.id as uncle FROM individual as childParent INNER JOIN individual as parentgp ON childParent.father_id=parentgp.id INNER JOIN individual as brother ON parentgp.mother_id = brother.mother_id where childparent.generation>1 AND childparent.father_id <> brother.id