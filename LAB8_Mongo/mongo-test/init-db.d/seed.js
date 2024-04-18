db.createUser({
  user: "umovies",
  pwd: "pmovies",   
  roles: [
    { role: "readWrite", db: "moviesdb" } 
  ]
});
db.test.drop();
db.test.insertMany([
  {
    _id: 1,
    name: 'Ken',
    age: 40
  },
  {
    _id: 2,
    name: 'Ben',
    age: 41
  }
])
