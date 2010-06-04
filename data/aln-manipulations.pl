#!/usr/bin/perl -w

# (1) Pretty-print clustalw with matching symbols
# (2) Allows designation of new reference sequence
# (3) Slice columns
# (4) Remove constant sites
# (5) Remove a sequence
# (6) Change format
# (7) Informative sites?
# (8) Added Waterson's theda estimation (-vw) for pop data (Steiper's data)
# (9) Bug fix: change reference sequence (8/12/2004)

use strict;
use Bio::SeqIO;
use Bio::Seq;
use Bio::AlignIO;
use Bio::SimpleAlign;
use Data::Dumper;
use Getopt::Std;
use Bio::LocatableSeq;

my %opts;
getopts('qgms:vp:r:f:i:cwnk:', \%opts);
my $file = shift @ARGV;
my $in_format = $opts{i} || 'clustalw';
my $in = Bio::AlignIO->new(-file=>$file, -format=>$in_format); # standard in
my $out_format=$opts{f} || 'clustalw';
my $out = Bio::AlignIO->new(-format=>$out_format);
my $aln = $in->next_aln();
my $new_aln;
my @seq;
my $no_seqs = $aln->no_sequences();
#my $length = $aln->length();
if ($opts{n}){
    print $no_seqs, "\n";
    exit;
}

my $aln2 = new Bio::SimpleAlign();
my $consense;
if ($opts{c}){ # add consensus sequence, as a potential outgroup
    $consense = new Bio::LocatableSeq(-seq=>$aln->consensus_string(50),
				      -id=>"Consensus_50",
				      -start=>1,
				      -end=>$aln->length());
    $aln->add_seq($consense);
    $new_aln = $aln;
}

if ($opts{r}) { # Change reference sequence
    foreach my $seq ($aln->each_seq) { # print STDERR Dumper($seq);
	if ($seq->display_id eq $opts{r}) {
	    unshift @seq, $seq;
	    print STDERR "Reference changed to ", $seq[0]->display_id(), "\n";
	} else { 
	    push @seq, $seq;
	} 
    } #print STDERR Dumper(\@seq);
    foreach my $seq (@seq) {
	if ($opts{p} && ($seq->display_id eq $opts{p})) {  # remove a sequence
	    $aln->remove_seq($seq); 
	} elsif ($opts{k}) {  # selecting sequences
	    my @selected = split(/\s*,\s*/, $opts{k});
	    foreach my $id ( @selected ){
		next unless $seq->display_id eq $id;
		$aln2->add_seq($seq);
	    }
	} else {
	    $aln2->add_seq($seq);
	}
    }
} else {
    foreach my $seq ($aln->each_seq) { 
	push @seq, $seq;
    } 
    foreach my $seq (@seq) {
	if ($opts{p} && ($seq->display_id eq $opts{p})) {
	    $aln->remove_seq($seq); 
	} elsif ($opts{k}) { 
	    my @selected = split(/\s*,\s*/, $opts{k});
	    foreach my $id ( @selected ){
		next unless $seq->display_id eq $id; 
		$aln2->add_seq($seq);
	    }
	} else {
	    $aln2->add_seq($seq);
	}
    }
}

if ($opts{s}) { # get alignment slice
    my ($begin, $end) = split(/\|/, $opts{s});

    if ($opts{r}) {
	$new_aln=$aln2->slice($begin, $end);
    } else {
	$new_aln=$aln->slice($begin, $end);
    }
} else {
    if ($opts{r} || $opts{k}) {
	$new_aln=$aln2;
    } else {
	$new_aln=$aln;
    }
}

#print STDERR $new_aln->get_seq_by_pos(1)->display_id(), "\n";

# Extract variable sites
if ($opts{v}) { #delete constant sites 
    my $aln3 = new Bio::SimpleAlign();
    my $length = $new_aln->length(); 
    my $ntax=$new_aln->no_sequences();
    my $count_var = 0;

    my @flag;
    for(my $i= 1; $i<=$length; $i++){ # 2. flag the variable sites 
	my @char=();
	foreach my $seq ($new_aln->each_seq) { 
	    push @char, $seq->subseq($i,$i);
	}

	my $status = &column_status(\@char);

	if ($status->{gap} && $opts{g}) { push @flag, 0; next }
	if ($status->{constant})  { push @flag, 0; next }
	if (!$status->{informative} && $opts{q}) {push @flag, 0; next}	

	push @flag, 1;
    }
#    print Dumper(\@flag);
    if ($opts{w}){ # Calculating Watterson's Theda
	printf "No. polymorphic sites: S=%d,\tWatterson's Theda: %.6f\n", $count_var, &watterson($count_var, $length, $no_seqs);
	exit;
    } else {
	print STDERR "Variable sites:\n";
	for(my $i= 0; $i<$length; $i++){ 
	    print STDERR $i+1, "\t" if $flag[$i];
	} print STDERR "\n";
	for (my $i=1; $i<=$ntax;$i++){ # 3. extract variable sites
	    my $seq = $new_aln->get_seq_by_pos($i);
	    my $id = $seq->display_id;
	    my $seq_str = '';
	    my $end=0; 
	for(my $j= 0; $j<$length; $j++){ 
	    next unless $flag[$j]; 
	    $end++; 
	    $seq_str .= $seq->subseq($j+1,$j+1);
	}
	    my $loc_seq = new Bio::LocatableSeq(-seq=>$seq_str,
						-id=>$id,
						-start=>1,
						-end=>$end);
#	print Dumper ($loc_seq);
	    $aln3->add_seq($loc_seq);
	}	
	$new_aln=$aln3;
    }
}

if ($opts{m}) {
    $new_aln->match();
}

$new_aln->set_displayname_flat();
$out->write_aln($new_aln);

exit;

sub column_status {
    my %count;
    my $ref=shift;
    my @array=@$ref;
    my $st = {
	gap => 0,
	informative => 1,
	constant => 1
    };

    foreach my $char (@array){
#	next if $char =~ /[\-\?]/; 
	$count{$char}++;
	$st->{gap} = 1 if $char =~ /[\-\?]/;
    }

    my @keys = keys %count; 

    foreach my $ct (values %count) {
	if ($ct < 2) {
	    $st->{informative} = 0; # including gap
	    last;
	}
    }

#    if ($#keys && !$gap) { # variable and no gap
    if ($#keys) { # variable (including gaps)
	$st->{constant} = 0;
    }
    return $st;
}


sub watterson {  #Nei, 1987, p.255
    my ($s, $m, $N) = @_;
    return $s/$m/&cal_A($N);
}

sub cal_A {
    my $num = shift;
    my $A=0;
    for (my $i=1; $i<$num; $i++){
	$A += 1/$i;
    }
    return $A;
}









