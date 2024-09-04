#include <iostream>
#include <vector>
#include <stack>
#include <algorithm>
#include <map>
#include <set>


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
    map<int, set<int>> map;

    for (int i = 0; i <= c; i++) {
        map[i] = set<int>();
    }
    for (int i = 0; i < comp.size(); i++) {
        map[comp[i]].insert(i);
    }

    int res = 0;
    for (const auto& x : map) {
        if (x.second.size() == 1){
            //cout << "a" << endl;
            res++;
        } else if (x.second.size() > 1){
            //cout << "b" << endl;
            res += 2;
        }
    }
    cout << res << endl;

    return 0;
}