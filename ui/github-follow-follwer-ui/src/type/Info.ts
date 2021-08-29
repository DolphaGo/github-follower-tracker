declare interface Info {
  neighbors : List<Member>
  onlyFollowers : List<Member>
  onlyFollowings : List<Member>
}

declare interface Member{
  githubLogin : string
  url : string
}

declare interface List<T>{
  list : T[]
}
