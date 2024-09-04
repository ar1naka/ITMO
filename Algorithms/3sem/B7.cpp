#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;
struct edge {
    int u;
    int v;
    int w;
};
vector<int> parent;

bool compareTasks(const edge& a, const edge& b) {
    return a.w < b.w;
}

int find(int v) {
    if (v == parent[v])
        return v;
    parent[v] = find(parent[v]);
    return parent[v];
}
void union_set(int u, int v) {
    u = find(u);
    v = find(v);
    parent[u] = v;
}
int main(){
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int n, m;
    cin >> n >> m;
    vector<edge> graph(m);
    parent.resize(n+1);
    for(int i = 0; i < m;i++){
        int u, v, w;
        cin >> u >> v >> w;
        graph[i].u = u;
        graph[i].v = v;
        graph[i].w = w;
    }
    sort(graph.begin(), graph.end(), compareTasks);
    for (int i = 0; i < n; i++) {
        parent[i] = i;
    }
    vector<int> g;
    long long result = 0;
    for (edge x : graph){
        int u = x.u;
        int v = x.v;
        int w = x.w;
        if (find(u) != find(v)){
            union_set(u, v);
            result += w;
        }
    }
    cout << result;
}