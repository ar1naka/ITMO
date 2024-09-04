#include <iostream>
#include <vector>
using namespace std;

vector<vector<int>> res;
vector<int> visited;
vector<vector<int>> xs;
vector<pair<int, int>> edges;


void dfs(int v, vector<int>& x) {
    visited[v] = 1;
    x.push_back(v);

    for (int u: res[v])
        if (!visited[u]) dfs(u, x);
}

int main() {
    int n, m;
    cin >> n >> m;

    res.resize(n + 1);
    visited.resize(n + 1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;
        res[u].push_back(v);
        res[v].push_back(u);
    }

    for (int v = 1; v <= n; v++) {
        if (visited[v] != 0) continue;
        vector<int> x1;
        dfs(v, x1);
        xs.push_back(x1);
    }

    cout << xs.size() - 1 << endl;
    for (int i = 0; i < xs.size() - 1; i++) {
        cout << xs[i].back() << " " << xs[i + 1].back() << endl;
    }

    return 0;
}