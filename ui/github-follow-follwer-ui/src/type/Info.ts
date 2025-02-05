interface Info {
  neighbors: { list: Member[] };
  onlyFollowers: { list: Member[] };
  onlyFollowings: { list: Member[] };
}

interface Member {
  githubLogin: string;
  url: string;
}