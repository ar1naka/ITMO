#pragma GCC optimize("Ofast")
 
#include <vector>
#include <climits>
#include <iostream>
#include <algorithm>
 
using namespace std;
 
vector<int> topSort;
vector<bool> used;
vector<int> dist;
vector<vector<pair<int, int>>> graph;
 
void dfs(int u) {
    used[u] = true;
    for (pair<int, int> p : graph[u]) {
        if (!used[p.first]) {
            dfs(p.first);
        }
    }
    topSort.push_back(u);
}
 
int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
 
    int n, m, s, t;
    cin >> n >> m >> s >> t;
    --s;
    --t;
 
    graph.resize(n);
    used.resize(n, false);
    dist.resize(n, INT_MAX);
 
    for (int i = 0; i < m; i++) {
        int b, e, w;
        cin >> b >> e >> w;
        graph[b-1].emplace_back(e-1, w);
    }
 
    dfs(s);
    reverse(topSort.begin(), topSort.end());
 
    dist[s] = 0;
    for (int u : topSort) {
        for (auto p : graph[u]) {
            int v = p.first;
            dist[v] = min(dist[v], dist[u] + p.second);
        }
    }
 
    if (dist[t] == INT_MAX) {
        cout << "Unreachable" << endl;
    } else {
        cout << dist[t] << endl;
    }
    return 0;
}