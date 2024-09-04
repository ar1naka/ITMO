#include <iostream>
#include <vector>
#include <set>
#include <climits>
#pragma GCC optimize("O3")
using namespace std;

vector<vector<pair<int, int>>> graph;
vector<int> dist;

void d(int start) {
    dist[start] = 0;
    set<pair<int, int>> q;
    q.emplace(0, start);

    while (!q.empty()) {
        int x = q.begin()->second;
        q.erase(q.begin());

        for (auto edge : graph[x]) {
            int a = edge.first;
            int b = edge.second;
            if (dist[x] + b < dist[a]) {
                q.erase({dist[a], a});
                dist[a] = dist[x] + b;
                q.emplace(dist[a], a);
            }
        }
    }
}


int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int n, m;
    scanf("%d %d", &n, &m);
    graph.resize(n+1);
    for (int i = 0; i < m; i++) {
        int b, e, w;
        scanf("%d %d %d", &b, &e, &w);
        graph[b].emplace_back(e, w);
        graph[e].emplace_back(b, w);
    }
    dist.resize(n+1, INT_MAX);
    d(1);
    for (int i = 1; i <= n; i++) {
        cout << dist[i] << " ";
    }
    return 0;
}