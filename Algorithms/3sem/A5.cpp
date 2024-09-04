#include <iostream>
#include <vector>
#include <queue>
#include <algorithm>
#pragma GCC optimize("O3")
using namespace std;

vector<vector<int>> graph;
vector<int> dist;
vector<int> p;
vector<int> res, used;
int n;

int bfs(int s, int t) {
    dist[s] = 0;
    used[s] = true;
    queue<int> q;
    q.push(s);

    while (!q.empty()) {
        int v = q.front();
        q.pop();
        for (int u : graph[v]) {
            if (!used[u]) {
                used[u] = true;
                p[u] = v;
                dist[u] = dist[v] + 1;
                q.push(u);
            }
        }
    }
    if (dist[t] == n + 1) {
        return -1;
    }
    while (t != -1) {
        res.push_back(t);
        t = p[t];
    }
    reverse(res.begin(), res.end());
    return 1;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int m;
    scanf("%d %d", &n, &m);
    graph.resize(n + 1);
    p.resize(n + 1, -1);
    used.resize(n + 1);

    for (int i = 0; i < m; i++) {
        int b, e;
        scanf("%d %d", &b, &e);
        graph[b].emplace_back(e);
    }

    int s, t;
    scanf("%d %d", &s, &t);
    dist.resize(n + 1, n + 1);

    if (bfs(s, t) == 1) {
        cout << res.size() - 1 << endl;
        for (int i = 0; i < res.size(); i++) {
            cout << res[i] << " ";
        }
    } else {
        cout << -1 << endl;
    }
    return 0;
}
 