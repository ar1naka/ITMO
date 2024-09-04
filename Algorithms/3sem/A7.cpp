#include <iostream>
#include <vector>
#include <algorithm>
#include <cmath>
#include <iomanip>
#include <climits>

using namespace std;
struct edge {
    int x;
    int y;
};
int dis(int x1, int y1, int x2, int y2){
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
}

int main(){
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int n;
    cin >> n;
    vector<edge> graph(n);
    for (int i = 0; i < n;i++){
        int x, y;
        cin >> x >> y;
        graph[i].x = x;
        graph[i].y = y;
    }
    vector<bool> visited(n, false);
    vector<int> min(n, INT_MAX);
    double result = 0;
    vector<int> end(n, -1);
    min[0] = 0;
    for (int i = 0; i < n; i++){
        int u = -1;
        for (int j = 0; j < n; j++){
            if (!visited[j] && (u == -1 || min[j] < min[u])){
                u = j;
            }
        }
        visited[u] = true;
        if(u == 0){
            result += 0;
        } else {
            result += sqrt(dis(graph[u].x, graph[u].y,
                               graph[end[u]].x, graph[end[u]].y));
        }
        for (int v = 0; v < n; v++){
            int tmp = dis(graph[u].x, graph[u].y, graph[v].x, graph[v].y);
            if (tmp < min[v]){
                min[v] = tmp;
                end[v] = u;
            }
        }
    }
    cout << fixed << setprecision(11) << result;
}