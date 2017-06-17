SELECT childparent.id as sibiling1, childparent.father_id as parent, brother.id as sibiling2 FROM individual as childparent
INNER JOIN individual as brother
ON childparent.father_id = brother.father_id
where childparent.generation>1 AND childparent.id <> brother.id


union

SELECT childparent.id as sibiling1, childparent.mother_id as parent, brother.id as sibiling2 FROM individual as childparent
INNER JOIN individual as brother
ON childparent.mother_id = brother.mother_id
where childparent.generation>1 AND childparent.id <> brother.id

order by parent

--SELECT childparent.id as sibiling1, childparent.father_id as parent, brother.id as sibiling2 FROM individual as childparent INNER JOIN individual as brother ON childparent.father_id = brother.father_id where childparent.generation>1 AND childparent.id <> brother.id union SELECT childparent.id as sibiling1, childparent.mother_id as parent, brother.id as sibiling2 FROM individual as childparent INNER JOIN individual as brother  ON childparent.mother_id = brother.mother_id where childparent.generation>1 AND childparent.id <> brother.id order by parent