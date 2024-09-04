#include <iostream>
#include <vector>
#include <stack>
#include <algorithm>
using namespace std;
vector<bool> used;
void dfs1(int v,  stack<int>& order, vector<vector<int>>& list) {
    used[v] = true;
    for (int i : list[v]) {
        if (!used[i]) {
            dfs1(i, order, list);
        }
    }
    order.push(v);
}

void dfs2(int v, vector<int>& comp, vector<vector<int>>& list, int c) {
    used[v] = true;
    comp[v] = c;
    for (int i : list[v]) {
        if (!used[i]) {
            dfs2(i,  comp, list, c);
        }
    }
}

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<int>> g1(n);
    vector<vector<int>> g2(n);

    for (int i = 0; i < m; i++) {
        int a, b;
        cin >> a >> b;
        g1[a - 1].push_back(b - 1);
        g2[b - 1].push_back(a - 1);
    }

    used.assign (n, false);
    stack<int> p;
    for (int i = 0; i < n; i++) {
        if (!used[i]) {
            dfs1(i, p, g1);
        }
    }

    vector<int> comp(n, -1);

    int c = 0;
    used.assign (n, false);
    for (int i = 0; i < n;++i){
        int v = p.top();
        p.pop();
        if (!used[v]) {
            dfs2(v, comp, g2, c);
            c++;
        }
    }

    cout << c << endl;
    for (int x : comp) {
        cout << x + 1 << " ";
    }
    cout << endl;

    return 0;
}
 