#pragma GCC optimize("O3")

#include <set>
#include <vector>
#include <iostream>

using namespace std;

struct edge {
    int a;
    int b;
};

set<int> br;
vector<int> d, h, used;
vector<vector<edge>> graph;

size_t count(int v, int target) {
    size_t result = 0;
    for (auto [a, b] : graph[v]) {
        if (a == target) {
            result++;
        }
    }
    return result;
}

void dfs(int v, int p = -1) {
    used[v] = true;
    d[v] = h[v] = (p != -1 ? h[p] + 1 : 0);
    for (auto edge : graph[v]) {
        if (edge.a != p) {
            if (used[edge.a]) {
                d[v] = min(d[v], h[edge.a]);
            } else {
                dfs(edge.a, v);
                d[v] = min(d[v], d[edge.a]);
                if (h[v] < d[edge.a] && count(v, edge.a) == 1) {
                    br.insert(edge.b);
                }
            }
        }
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + 1);
    for (int i = 0; i < m; i++) {
        int a, b;
        cin >> a >> b;
        graph[a].push_back({b, i + 1});
        graph[b].push_back({a, i + 1});
    }

    d.resize(n + 1);
    h.resize(n + 1);
    used.resize(n + 1);
    for (int i = 1; i <= n; i++){
        if (!used[i]) {
            dfs(i, -1);
        }
    }

    cout << br.size() << endl;
    for (int x : br){
        cout << x << " ";
    }

    return 0;
}