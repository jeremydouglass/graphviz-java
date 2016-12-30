/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Attribute;
import guru.nidi.graphviz.attribute.MutableAttributed;

import java.util.*;

public class MutableGraph implements Linkable, MutableLinkSource<MutableGraph>, LinkTarget {
    protected boolean strict;
    protected boolean directed;
    protected boolean cluster;
    protected Label label;
    protected final Set<MutableNode> nodes;
    protected final Set<MutableGraph> subgraphs;
    protected final List<Link> links;
    protected final MutableAttributed<MutableGraph> generalAttrs;
    protected final MutableAttributed<MutableGraph> nodeAttrs;
    protected final MutableAttributed<MutableGraph> linkAttrs;
    protected final MutableAttributed<MutableGraph> graphAttrs;

    public MutableGraph() {
        this(false, false, false, Label.of(""), new LinkedHashSet<>(), new LinkedHashSet<>(), new ArrayList<>(),
                null, null, null, null);
        CreationContext.current().ifPresent(ctx -> getGeneralAttrs().addAttr(ctx.graphs()));
    }

    protected MutableGraph(boolean strict, boolean directed, boolean cluster, Label label,
                           Set<MutableNode> nodes, Set<MutableGraph> subgraphs, List<Link> links,
                           Attribute generalAttrs, Attribute nodeAttrs, Attribute linkAttrs, Attribute graphAttrs) {
        this.strict = strict;
        this.directed = directed;
        this.cluster = cluster;
        this.label = label;
        this.nodes = nodes;
        this.subgraphs = subgraphs;
        this.links = links;
        this.generalAttrs = new SimpleMutableAttributed<>(this, generalAttrs);
        this.nodeAttrs = new SimpleMutableAttributed<>(this, nodeAttrs);
        this.linkAttrs = new SimpleMutableAttributed<>(this, linkAttrs);
        this.graphAttrs = new SimpleMutableAttributed<>(this, graphAttrs);
    }

    public MutableGraph copy() {
        return new MutableGraph(strict, directed, cluster, label,
                new LinkedHashSet<>(nodes), new LinkedHashSet<>(subgraphs), new ArrayList<>(links),
                generalAttrs, nodeAttrs, linkAttrs, graphAttrs);
    }

    public MutableGraph setStrict() {
        strict = true;
        return this;
    }

    public MutableGraph setDirected() {
        directed = true;
        return this;
    }

    public MutableGraph setCluster() {
        cluster = true;
        return this;
    }

    public MutableGraph setLabel(Label label) {
        this.label = label;
        return this;
    }

    public MutableGraph setLabel(String name) {
        return setLabel(Label.of(name));
    }

    MutableGraph addNode(MutableNode node) {
        nodes.add(node);
        return this;
    }

    public MutableGraph addGraphs(MutableGraph... subgraphs) {
        for (final MutableGraph subgraph : subgraphs) {
            addGraph(subgraph);
        }
        return this;
    }

    MutableGraph addGraph(MutableGraph subgraph) {
        subgraphs.add(subgraph);
        return this;
    }

    public MutableGraph add(LinkSource... sources) {
        for (final LinkSource source : sources) {
            add(source);
        }
        return this;
    }

    public MutableGraph add(LinkSource source) {
        if (source instanceof MutableNode) {
            return addNode((MutableNode) source);
        } else if (source instanceof MutableNodePoint) {
            return addNode(((MutableNodePoint) source).node);
        } else if (source instanceof MutableGraph) {
            return addGraph((MutableGraph) source);
        }
        throw new IllegalArgumentException("Unknown source of type " + source.getClass());
    }

    public MutableGraph addLink(LinkTarget... targets) {
        for (final LinkTarget target : targets) {
            addLink(target);
        }
        return this;
    }

    public MutableGraph addLink(LinkTarget target) {
        final Link link = target.linkTo();
        links.add(Link.between(this, link.to).attr(link.attributes));
        return this;
    }

    @Override
    public Collection<Link> getLinks() {
        return links;
    }

    @Override
    public Link linkTo() {
        return Link.to(this);
    }

    public boolean isStrict() {
        return strict;
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean isCluster() {
        return cluster;
    }

    public Label label() {
        return label;
    }

    public MutableAttributed<MutableGraph> getGeneralAttrs() {
        return generalAttrs;
    }

    public MutableAttributed<MutableGraph> getNodeAttrs() {
        return nodeAttrs;
    }

    public MutableAttributed<MutableGraph> getLinkAttrs() {
        return linkAttrs;
    }

    public MutableAttributed<MutableGraph> getGraphAttrs() {
        return graphAttrs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MutableGraph graph = (MutableGraph) o;

        if (strict != graph.strict) {
            return false;
        }
        if (directed != graph.directed) {
            return false;
        }
        if (cluster != graph.cluster) {
            return false;
        }
        if (!label.equals(graph.label)) {
            return false;
        }
        if (!nodes.equals(graph.nodes)) {
            return false;
        }
        if (!subgraphs.equals(graph.subgraphs)) {
            return false;
        }
        if (!links.equals(graph.links)) {
            return false;
        }
        if (!generalAttrs.equals(graph.generalAttrs)) {
            return false;
        }
        if (!nodeAttrs.equals(graph.nodeAttrs)) {
            return false;
        }
        if (!linkAttrs.equals(graph.linkAttrs)) {
            return false;
        }
        return graphAttrs.equals(graph.graphAttrs);

    }

    @Override
    public int hashCode() {
        int result = (strict ? 1 : 0);
        result = 31 * result + (directed ? 1 : 0);
        result = 31 * result + (cluster ? 1 : 0);
        result = 31 * result + label.hashCode();
        result = 31 * result + nodes.hashCode();
        result = 31 * result + subgraphs.hashCode();
        result = 31 * result + links.hashCode();
        result = 31 * result + generalAttrs.hashCode();
        result = 31 * result + nodeAttrs.hashCode();
        result = 31 * result + linkAttrs.hashCode();
        result = 31 * result + graphAttrs.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new Serializer(this).serialize();
    }
}
