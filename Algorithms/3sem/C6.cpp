#pragma GCC optimize("O3")

#include <vector>
#include <iostream>

using namespace std;

struct edge {
    int a;
    int b;
    int w;
};

vector<int> d;
vector<edge> g;

void ford_bellman(int n, int m, int v) {
    d[v] = 0;
    for (size_t i = 0; i < n - 1; i++) {
        for (size_t j = 0; j < m; j++) {
            if (d[g[j].a] < 30000) {
                d[g[j].b] = min(d[g[j].a] + g[j].w, d[g[j].b]);
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

    for (size_t i = 0; i < m; i++) {
        int a, b, w;
        cin >> a >> b >> w;
        g.push_back({--a, --b, w});
    }

    d.resize(n, 30000);
    ford_bellman(n, m, 0);

    for (int dist : d) {
        cout << dist << " ";
    }

    return 0;
}