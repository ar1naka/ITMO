#include <iostream>
#include <vector>
#pragma GCC optimize("Ofast")

using  namespace std;

int n, m;
vector<vector<int>> res;
vector<int> used;

void dfs(int v){
    used[v] = 1;
    for (int i : res[v]) {
        if (used[i] == 0){
            cout << v << " " << i << endl;
            dfs(i);
        }
    }
    used[v] = 2;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int x, y;
    cin >> n >> m;
    used.resize(n + 1);
    res.resize(n + 1, vector<int>());
    for (int i = 0; i < m; i++) {
        cin >> x >> y;
        res[x].push_back(y);
        res[y].push_back(x);
    }
    dfs(1);
    return 0;
}